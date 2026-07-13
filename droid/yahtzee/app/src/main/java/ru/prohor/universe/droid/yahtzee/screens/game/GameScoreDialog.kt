package ru.prohor.universe.droid.yahtzee.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.prohor.universe.droid.yahtzee.domain.game.CHANCE
import ru.prohor.universe.droid.yahtzee.domain.game.Combination
import ru.prohor.universe.droid.yahtzee.domain.game.FixedValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.FreeValue
import ru.prohor.universe.droid.yahtzee.domain.game.FreeValueCombination
import ru.prohor.universe.droid.yahtzee.domain.game.GameState
import ru.prohor.universe.droid.yahtzee.domain.game.SimpleCombination
import ru.prohor.universe.droid.yahtzee.ui.AppButton
import ru.prohor.universe.droid.yahtzee.ui.BoxSpacer
import ru.prohor.universe.droid.yahtzee.ui.HorizontalSpacer
import ru.prohor.universe.droid.yahtzee.ui.VerticalSpacer

@Composable
fun ScoreDialog(
    combination: Combination,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            color = Color(0xAF1D1D1F)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VerticalSpacer(40)

                Text(
                    text = combination.readableName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )

                VerticalSpacer(20)

                val onSelect: (Int) -> Unit = { value ->
                    GameState.setScore(combination, value)
                    onDismiss()
                }

                when (combination) {
                    is SimpleCombination -> SimpleCombinationInput(combination, onSelect, onDismiss)
                    is FixedValueCombination -> FixedValueInput(combination, onSelect, onDismiss)
                    is FreeValueCombination -> FreeValueInput(combination, onSelect, onDismiss)
                    is CHANCE -> FreeValueInput(combination, onSelect, onDismiss)
                }

                VerticalSpacer(60)
            }
        }
    }
}

@Composable
private fun SimpleCombinationInput(
    combination: SimpleCombination,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    BoxWithConstraints {
        val diceSize = minOf(60.dp, (maxWidth - 32.dp) / 5)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(diceSize)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onSelect((index + 1) * combination.base)
                        }
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DiceFace(combination.base)
                }
            }
        }
    }

    VerticalSpacer(24)

    Row {
        AppButton(
            text = "Отмена",
            onClick = onDismiss
        )

        HorizontalSpacer(20)

        ExpandingZeroButton(onSelect)
    }
}

@Composable
private fun DiceFace(value: Int) {
    val visibleDots = when (value) {
        1 -> setOf(4)
        2 -> setOf(0, 8)
        3 -> setOf(0, 4, 8)
        4 -> setOf(0, 2, 6, 8)
        5 -> setOf(0, 2, 4, 6, 8)
        6 -> setOf(0, 2, 3, 5, 6, 8)
        else -> emptySet()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(3) { column ->
                    val index = row * 3 + column

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (index in visibleDots)
                                    Color.Black
                                else
                                    Color.Transparent
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun FreeValueInput(
    combination: FreeValue,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val number = text.toIntOrNull()
    val isValid = number != null && combination.validate(number)
    val isError = text.isNotEmpty() && !isValid

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            onValueChange = {
                text = it.take(2)
            },
            textStyle = MaterialTheme.typography.headlineSmall,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF2F2F2)
            ),
            isError = isError,
            modifier = Modifier.width(220.dp)
        )

        VerticalSpacer(8)

        val error = if (isError) "Некорректное значение" else ""
        Text(
            text = error,
            modifier = Modifier.height(20.dp),
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )

        if (combination != CHANCE) {
            VerticalSpacer(16)

            AppButton(
                text = "0",
                containerColor = Color(0xFFFF9800),
                modifier = Modifier.width(220.dp),
                onClick = {
                    onSelect(0)
                }
            )
        }

        VerticalSpacer(16)

        Row {
            AppButton(
                text = "Отмена",
                onClick = onDismiss
            )

            BoxSpacer(12)

            AppButton(
                text = "Сохранить",
                onClick = {
                    if (isValid) onSelect(number)
                }
            )
        }
    }
}

@Composable
private fun RowScope.ExpandingZeroButton(
    onSelect: (Int) -> Unit
) {
    AppButton(
        text = "0",
        containerColor = Color(0xFFFF9800),
        modifier = Modifier.weight(1f),
        onClick = {
            onSelect(0)
        }
    )
}

@Composable
private fun FixedValueInput(
    combination: FixedValueCombination,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val value = combination.fixedValue
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppButton(
            text = "0",
            containerColor = Color(0xFFFF9800),
            onClick = { onSelect(0) },
            modifier = Modifier.width(120.dp)
        )

        AppButton(
            text = value.toString(),
            containerColor = Color(0xFF4CAF50),
            onClick = { onSelect(value) },
            modifier = Modifier.width(120.dp)
        )
    }

    VerticalSpacer(16)

    Row {
        AppButton(
            text = "Отмена",
            onClick = onDismiss,
            modifier = Modifier.width(252.dp),
        )
    }
}
