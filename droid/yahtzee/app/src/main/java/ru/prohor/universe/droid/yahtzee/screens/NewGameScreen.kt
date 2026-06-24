package ru.prohor.universe.droid.yahtzee.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.domain.team.IndexedTeam
import ru.prohor.universe.droid.yahtzee.domain.team.MAX_TEAM_NAME_LENGTH
import ru.prohor.universe.droid.yahtzee.domain.team.Team
import ru.prohor.universe.droid.yahtzee.domain.team.TeamColor
import ru.prohor.universe.droid.yahtzee.domain.team.TeamTemplate
import ru.prohor.universe.droid.yahtzee.domain.team.TeamTemplatesState
import ru.prohor.universe.droid.yahtzee.domain.team.TeamsState
import ru.prohor.universe.droid.yahtzee.ext.letIf
import ru.prohor.universe.droid.yahtzee.navigation.NavigationActions
import ru.prohor.universe.droid.yahtzee.ui.AppButton
import ru.prohor.universe.droid.yahtzee.ui.Background
import ru.prohor.universe.droid.yahtzee.ui.ExpandingSpacer
import ru.prohor.universe.droid.yahtzee.ui.VerticalSpacer
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun NewGameScreen(navController: NavController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showTemplatesDialog by remember { mutableStateOf(false) }
    var editingTeamIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            TeamsHeader(onBack = { NavigationActions.back(navController) })

            VerticalSpacer(16)

            TeamsList(
                modifier = Modifier.weight(1f),
                onEdit = { index ->
                    editingTeamIndex = index
                    showDialog = true
                }
            )

            VerticalSpacer(20)

            BottomButtons(
                onAddTeam = {
                    editingTeamIndex = null
                    showDialog = true
                },
                onStartGame = {
                    NavigationActions.startGame(navController, context)
                },
                onPressTemplates = {
                    showTemplatesDialog = true
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

        if (showTemplatesDialog) {
            TeamTemplatesDialog(
                onDismiss = {
                    showTemplatesDialog = false
                },
                onSelect = { template ->
                    TeamsState.save(Team(template.name, template.color), null)
                    showTemplatesDialog = false
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
            text = "Команды: ${TeamsState.count()}/${TeamsState.MAX_COUNT}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun TeamsList(
    modifier: Modifier = Modifier,
    onEdit: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        TeamsState.moveTeam(from.index, to.index)
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = TeamsState.count(),
            key = { index -> TeamsState.team(index).name }
        ) { index ->
            val indexedTeam = TeamsState.getAllIndexed().getOrNull(index) ?: return@items
            ReorderableItem(reorderState, key = indexedTeam.team.name) { isDragging ->
                TeamCard(
                    indexedTeam = indexedTeam,
                    isDragging = isDragging,
                    modifier = Modifier.draggableHandle(),
                    onEdit = { onEdit(indexedTeam.index) }
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    indexedTeam: IndexedTeam,
    isDragging: Boolean,
    modifier: Modifier,
    onEdit: () -> Unit
) {
    val elevation = if (isDragging) 8.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = indexedTeam.team.color.mainColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
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
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            TeamRemoveButton(indexedTeam)
            TeamDragHandle(
                tint = indexedTeam.team.color.textColor,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TeamRemoveButton(indexedTeam: IndexedTeam) {
    IconButton(
        onClick = { TeamsState.removeAt(indexedTeam.index) }
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Удалить команду",
            tint = indexedTeam.team.color.textColor
        )
    }
}

@Composable
private fun TeamDragHandle(
    tint: Color,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = {},
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = "Перетащить",
            tint = tint
        )
    }
}

@Composable
private fun BottomButtons(
    onAddTeam: () -> Unit,
    onStartGame: () -> Unit,
    onPressTemplates: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (TeamsState.isAdditionAvailable()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val templatesAvailable = TeamTemplatesState.isSuitableTemplatesPresent()
                if (templatesAvailable) {
                    AppButton(
                        text = "Шаблоны",
                        onClick = onPressTemplates,
                        modifier = Modifier.weight(1f)
                    )
                }

                AppButton(
                    text = if (templatesAvailable) "Добавить\nкоманду" else "Добавить команду",
                    onClick = onAddTeam,
                    modifier = Modifier.weight(1f)
                )
            }

            VerticalSpacer(12)
        }

        if (TeamsState.isAvailableToStartGame() || TeamsState.isShuffleAvailable()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (TeamsState.isShuffleAvailable()) {
                    AppButton(
                        text = "Перемешать",
                        onClick = { TeamsState.shuffle() },
                        modifier = Modifier.weight(1f)
                    )
                }

                AppButton(
                    text = "Начать игру",
                    onClick = onStartGame,
                    modifier = Modifier.weight(1f),
                    visible = TeamsState.isAvailableToStartGame()
                )
            }
        }
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

    val availableColors = TeamColor.entries.filter { it !in usedColors }
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
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
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
                        text = "Отмена",
                        onClick = onDismiss
                    )

                    AppButton(
                        text = "Добавить",
                        onClick = {
                            if (teamName.isBlank() || duplicatedName) return@AppButton

                            val team = Team(teamName, selectedColor)
                            TeamsState.save(team, editingTeamIndex)
                            onSave()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
        TeamColor.entries.forEach { color ->
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

@Composable
private fun TeamTemplatesDialog(
    onDismiss: () -> Unit,
    onSelect: (TeamTemplate) -> Unit
) {
    val templates = TeamTemplatesState.topTemplates()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Шаблоны команд",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                VerticalSpacer(20)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    templates.forEach { template ->
                        TeamTemplateCard(
                            template = template,
                            onClick = {
                                onSelect(template)
                            }
                        )

                        VerticalSpacer(12)
                    }
                }

                VerticalSpacer(20)

                AppButton(
                    text = "Закрыть",
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun TeamTemplateCard(
    template: TeamTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = template.color.mainColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 20.dp)
        ) {
            Text(
                text = template.name,
                color = template.color.textColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
