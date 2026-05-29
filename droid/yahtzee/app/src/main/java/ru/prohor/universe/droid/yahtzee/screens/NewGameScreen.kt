package ru.prohor.universe.droid.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.common.letIf
import ru.prohor.universe.droid.yahtzee.model.IndexedTeam
import ru.prohor.universe.droid.yahtzee.model.MAX_TEAM_NAME_LENGTH
import ru.prohor.universe.droid.yahtzee.model.Team
import ru.prohor.universe.droid.yahtzee.state.TeamsState
import ru.prohor.universe.droid.yahtzee.ui.shared.AppButton
import ru.prohor.universe.droid.yahtzee.ui.shared.Background
import ru.prohor.universe.droid.yahtzee.ui.shared.ExpandingSpacer
import ru.prohor.universe.droid.yahtzee.ui.shared.VerticalSpacer
import ru.prohor.universe.droid.yahtzee.ui.theme.TeamColor
import ru.prohor.universe.droid.yahtzee.ui.theme.teamColors

@Composable
fun NewGameScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var editingTeamIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            TeamsHeader(onBack = { navController.popBackStack() })

            VerticalSpacer(16)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                TeamsList(
                    onEdit = { index ->
                        editingTeamIndex = index
                        showDialog = true
                    }
                )
            }

            VerticalSpacer(20)

            BottomButtons(
                onAddTeam = {
                    editingTeamIndex = null
                    showDialog = true
                },
                onStartGame = {
                    if (TeamsState.isAvailableToStartGame()) {
                        navController.navigate("game")
                    }
                }
            )
        }

        if (showDialog) {
            AddTeamDialog(
                editingTeamIndex = editingTeamIndex,
                onDismiss = {
                    showDialog = false
                },
                onSave = {
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun TeamsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButton(
            text = "Назад",
            onClick = onBack
        )

        ExpandingSpacer()

        Text(
            text = "Команды: ${TeamsState.count()}/8",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun TeamsList(
    onEdit: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TeamsState.getAllIndexed().forEach {
            TeamCard(
                indexedTeam = it,
                onEdit = { onEdit(it.index) }
            )
        }
    }
}

@Composable
private fun TeamCard(
    indexedTeam: IndexedTeam,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = indexedTeam.team.color.mainColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = indexedTeam.team.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = indexedTeam.team.color.textColor,
                modifier = Modifier.width(180.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            TeamUpButton(indexedTeam)
            TeamDownButton(indexedTeam)
            TeamRemoveButton(indexedTeam)
        }
    }
}

@Composable
private fun TeamUpButton(indexedTeam: IndexedTeam) {
    if (indexedTeam.index == 0) {
        TeamActionPlaceholder()
        return
    }
    IconButton(
        onClick = { TeamsState.moveTeamUp(indexedTeam.index) }
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = indexedTeam.team.color.textColor
        )
    }
}

@Composable
private fun TeamDownButton(indexedTeam: IndexedTeam) {
    if (TeamsState.isLastIndex(indexedTeam.index)) {
        TeamActionPlaceholder()
        return
    }
    IconButton(
        onClick = { TeamsState.moveTeamDown(indexedTeam.index) }
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = indexedTeam.team.color.textColor
        )
    }
}

@Composable
private fun TeamRemoveButton(indexedTeam: IndexedTeam) {
    IconButton(
        onClick = { TeamsState.removeAt(indexedTeam.index) }
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = indexedTeam.team.color.textColor
        )
    }
}

@Composable
private fun TeamActionPlaceholder() {
    Box(modifier = Modifier.size(48.dp))
}

@Composable
private fun BottomButtons(
    onAddTeam: () -> Unit,
    onStartGame: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val shuffleAvailable = TeamsState.count() > 1
            if (shuffleAvailable) {
                AppButton(
                    text = "Перемешать",
                    onClick = { TeamsState.shuffle() },
                    modifier = Modifier.weight(1f)
                )
            }

            if (TeamsState.isAdditionAvailable()) {
                val addTeam = if (shuffleAvailable) "Добавить\nкоманду" else "Добавить команду"
                AppButton(
                    text = addTeam,
                    onClick = onAddTeam,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        VerticalSpacer(16)

        AppButton(
            text = "Начать игру",
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AddTeamDialog(
    editingTeamIndex: Int?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val editingTeam = editingTeamIndex?.let { TeamsState.team(it) }
    var teamName by remember { mutableStateOf(editingTeam?.name ?: "") }

    val usedColors = TeamsState.usedColors()
    editingTeam?.let { usedColors.remove(it.color) }

    val availableColors = teamColors.filter { it !in usedColors }
    var selectedColor by remember {
        mutableStateOf(editingTeam?.color ?: availableColors.random())
    }

    val duplicatedName = TeamsState.teams().any {
        it.name.equals(teamName, true) && it != editingTeam
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1D1D1F),
            modifier = Modifier.fillMaxWidth(0.96f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (editingTeam == null) "Новая команда" else "Редактирование",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                VerticalSpacer(20)

                TextField(
                    value = teamName,
                    onValueChange = { teamName = it.trim().take(MAX_TEAM_NAME_LENGTH) },
                    singleLine = true,
                    placeholder = { Text("Название команды") },
                    isError = duplicatedName,
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        errorContainerColor = Color(0xFFE8736B)
                    ),
                    textStyle = MaterialTheme.typography.titleMedium
                )

                if (duplicatedName) {
                    VerticalSpacer(8)
                    Text(
                        text = "Название уже занято",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                VerticalSpacer(24)
                ColorPicker(
                    selectedColor = selectedColor,
                    usedColors = usedColors,
                    onSelect = { selectedColor = it }
                )
                VerticalSpacer(28)

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppButton(
                        text = "Добавить",
                        onClick = {
                            if (teamName.isBlank() || duplicatedName) return@AppButton

                            val team = Team(teamName, selectedColor)
                            TeamsState.save(team, editingTeamIndex)
                            onSave()
                        }
                    )

                    AppButton(
                        text = "Отмена",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(
    selectedColor: TeamColor,
    usedColors: Set<TeamColor>,
    onSelect: (TeamColor) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        teamColors.forEach { color ->
            ColorCircle(
                color = color,
                isSelected = selectedColor == color,
                isUsed = color in usedColors,
                onClick = { onSelect(color) }
            )
        }
    }
}

@Composable
private fun ColorCircle(
    color: TeamColor,
    isSelected: Boolean,
    isUsed: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(52.dp)
            .clip(CircleShape)
            .background(color.mainColor.letIf(isUsed) { it.copy(alpha = 0.35f) })
            .border(
                width = if (isSelected) 4.dp else 2.dp,
                color = color.textColor,
                shape = CircleShape
            )
            .clickable(enabled = !isUsed) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (isUsed || isSelected) {
            Text(
                text = if (isSelected) "✓" else "x",
                color = color.textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
