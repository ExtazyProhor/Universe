package ru.prohor.universe.droid.yahtzee.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.domain.settings.SettingsState
import ru.prohor.universe.droid.yahtzee.navigation.NavigationActions
import ru.prohor.universe.droid.yahtzee.ui.AppButton
import ru.prohor.universe.droid.yahtzee.ui.AppText
import ru.prohor.universe.droid.yahtzee.ui.Background
import ru.prohor.universe.droid.yahtzee.ui.ExpandingSpacer
import ru.prohor.universe.droid.yahtzee.ui.VerticalSpacer

@Composable
fun SettingsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Header(
                onBack = {
                    NavigationActions.back(navController)
                }
            )

            VerticalSpacer(24)

            val context = LocalContext.current

            SettingsItem(
                title = "Сохранять порядок команд при перемешивании",
                checked = SettingsState.settings.keepTeamsOrderOnShuffle,
                onCheckedChange = {
                    SettingsState.update(context) {
                        it.copy(
                            keepTeamsOrderOnShuffle = !it.keepTeamsOrderOnShuffle
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun Header(
    onBack: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AppButton(
            text = "Назад",
            onClick = onBack
        )

        ExpandingSpacer()

        AppText(
            text = "Настройки",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.65f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCheckedChange(!checked)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White,
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}
