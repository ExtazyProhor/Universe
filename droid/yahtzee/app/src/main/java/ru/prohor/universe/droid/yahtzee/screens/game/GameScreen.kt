package ru.prohor.universe.droid.yahtzee.screens.game

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.model.ALL_COMPLEX_COMBINATIONS
import ru.prohor.universe.droid.yahtzee.model.Combination
import ru.prohor.universe.droid.yahtzee.model.CombinationItem
import ru.prohor.universe.droid.yahtzee.model.MetaCombination
import ru.prohor.universe.droid.yahtzee.model.SimpleCombination
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.state.GameState
import ru.prohor.universe.droid.yahtzee.state.TeamsState
import ru.prohor.universe.droid.yahtzee.ui.shared.AppButton
import ru.prohor.universe.droid.yahtzee.ui.shared.Background
import ru.prohor.universe.droid.yahtzee.ui.shared.BoxSpacer
import ru.prohor.universe.droid.yahtzee.ui.shared.ExpandingSpacer
import ru.prohor.universe.droid.yahtzee.ui.shared.VerticalSpacer

@Composable
fun GameScreen(navController: NavController) {
    BackHandler {}
    GameScreenRender(navController).Render()
}

private class GameScreenRender(
    private val navController: NavController
) {
    private var editingCombination by mutableStateOf<Combination?>(null)
    private val teamsCount = TeamsState.count()
    private val isGameFinished = GameState.isGameFinished()

    @Composable
    fun Render() {
        Box(modifier = Modifier.fillMaxSize()) {
            Background()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                TopBar {
                    navController.popBackStack()
                }

                VerticalSpacer(20)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    CombinationGroup(MetaCombination.entries, FontWeight.Bold)
                    CombinationGroupSeparator()
                    CombinationGroup(SimpleCombination.entries)
                    CombinationGroupSeparator()
                    CombinationGroup(ALL_COMPLEX_COMBINATIONS)
                }

                VerticalSpacer(24)
                if (isGameFinished) {
                    FinishButton()
                }
            }

            editingCombination?.let { combination ->
                ScoreDialog(
                    combination = combination,
                    onDismiss = {
                        editingCombination = null
                    }
                )
            }
        }
    }

    @Composable
    private fun TopBar(
        onCancelGame: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (GameState.isUndoAvailable()) {
                AppButton(
                    text = "Отменить ход",
                    onClick = {
                        GameState.undoLastMove()
                    }
                )
            } else if (GameState.isGameStarted()) {
                AppButton(
                    text = "Отменить игру",
                    onClick = onCancelGame
                )
            }

            ExpandingSpacer()

            val currentTeam = GameState.currentTeam()
            if (isGameFinished) {
                Box(
                    modifier = Modifier
                        .width(178.dp)
                        .height(54.dp)
                )
                return
            }

            Box(
                modifier = Modifier
                    .width(178.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(currentTeam.color.mainColor)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentTeam.name,
                    color = currentTeam.color.textColor,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    private fun CombinationGroupSeparator() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )
    }

    @Composable
    private fun CombinationGroup(
        combinations: List<CombinationItem>,
        fontWeight: FontWeight? = null
    ) {
        combinations.forEachIndexed { index, combination ->
            CombinationRow(combination, fontWeight)

            if (index != combinations.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )
            }
        }
    }

    @Composable
    private fun CombinationRow(combination: CombinationItem, fontWeight: FontWeight? = null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = combination.readableName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = fontWeight
            )
            ExpandingSpacer()
            ScoresRow(combination)
        }
    }

    @Composable
    private fun ScoresRow(combination: CombinationItem) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val previousTeam = GameState.previousTeam()
            val currentTeam = GameState.currentTeam()
            val nextTeam = GameState.nextTeam()

            if (teamsCount >= 2) {
                SecondaryScoreBox(
                    team = previousTeam,
                    combination = combination
                )
            }

            ScoreBox(
                combination = combination,
                value = GameState.score(currentTeam, combination)?.toString()
            )

            if (teamsCount >= 3) {
                SecondaryScoreBox(
                    team = nextTeam,
                    combination = combination
                )
            }
        }
    }

    @Composable
    private fun SecondaryScoreBox(
        team: Team,
        combination: CombinationItem
    ) {
        val value = GameState.score(team, combination)
        if (value == null) {
            BoxSpacer(54)
            return
        }

        val color = team.color

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.lightColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                color = color.darkColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    @Composable
    private fun ScoreBox(
        combination: CombinationItem,
        value: String?
    ) {
        val value = if (value == null && combination is MetaCombination) {
            Log.e("bug", "meta combination $combination has null value")
            "er"
        } else {
            value
        }

        val color = GameState.currentTeam().color
        val editableCombination = value == null
        val backgroundColor = if (editableCombination) color.mainColor else color.lightColor
        val textColor = if (editableCombination) color.textColor else color.darkColor

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .clickable(
                    enabled = editableCombination,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    editingCombination = combination as Combination
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (editableCombination) "+" else value,
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    @Composable
    private fun FinishButton() {
        val context = LocalContext.current
        AppButton(
            text = "Завершить",
            onClick = {
                GameState.saveGame(context)
                navController.navigate("finish") {
                    popUpTo("game") {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFF36C23D)
        )

        VerticalSpacer(20)
    }
}
