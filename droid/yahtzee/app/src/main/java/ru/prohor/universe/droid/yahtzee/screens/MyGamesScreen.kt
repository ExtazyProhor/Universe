package ru.prohor.universe.droid.yahtzee.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.prohor.universe.droid.yahtzee.api.ApiResult
import ru.prohor.universe.droid.yahtzee.api.GameSender
import ru.prohor.universe.droid.yahtzee.domain.storage.GameDescription
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGamesState
import ru.prohor.universe.droid.yahtzee.ui.AppButton
import ru.prohor.universe.droid.yahtzee.ui.Background
import ru.prohor.universe.droid.yahtzee.ui.BoxSpacer
import ru.prohor.universe.droid.yahtzee.ui.ErrorDialog
import ru.prohor.universe.droid.yahtzee.ui.ExpandingSpacer
import ru.prohor.universe.droid.yahtzee.ui.HorizontalSpacer
import ru.prohor.universe.droid.yahtzee.ui.VerticalSpacer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MyGamesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Header(
                gamesCount = SavedGamesState.games().size,
                showSendAll = SavedGamesState.games().isNotEmpty(),
                onBack = {
                    navController.popBackStack()
                },
                onSendAll = {
                    scope.launch {
                        val result = GameSender.sendAll(context)
                        if (result is ApiResult.Error) {
                            error = result.message
                        }
                    }
                }
            )

            VerticalSpacer(16)

            if (SavedGamesState.games().isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет сохранённых игр",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                return
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SavedGamesState.games().forEach { GameCard(it) }
            }
        }

        error?.let {
            ErrorDialog(
                message = it,
                onDismiss = {
                    error = null
                }
            )
        }
    }
}

@Composable
private fun Header(
    gamesCount: Int,
    showSendAll: Boolean,
    onBack: () -> Unit,
    onSendAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppButton(
            text = "Назад",
            onClick = onBack
        )

        if (showSendAll) {
            HorizontalSpacer(8)

            ExpandingSpacer()

            AppButton(
                text = sendAllText(gamesCount),
                imageVector = Icons.AutoMirrored.Filled.Send,
                onClick = onSendAll
            )
        }
    }
}

private fun sendAllText(count: Int): String {
    val count = when {
        count > 99 -> "99+"
        else -> "$count"
    }
    return "Отправить ($count)"
}

@Composable
private fun GameCard(game: GameDescription) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5C591)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimestamp(game.finish),
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = Color.Black
            )

            BoxSpacer(6)

            Text(
                text = game.teams.toString(),
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

private fun formatTimestamp(timestamp: Long): String {
    return Instant
        .ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
