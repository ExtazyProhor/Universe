package ru.prohor.universe.droid.yahtzee.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.prohor.universe.droid.yahtzee.api.ApiResult
import ru.prohor.universe.droid.yahtzee.api.YahtzeeApi
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.ui.shared.AppButton
import ru.prohor.universe.droid.yahtzee.ui.shared.Background
import ru.prohor.universe.droid.yahtzee.ui.shared.VerticalSpacer

@Composable
fun AuthScreen(navController: NavController) {
    var key by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AuthCard(
                key = key,
                error = error,
                loading = loading,
                onKeyChanged = {
                    key = it.take(64)
                    error = ""
                },
                onSubmit = {
                    scope.launch {
                        loading = true
                        error = ""

                        val result = YahtzeeApi.validateKey(key)

                        when (result) {
                            ApiResult.Success -> {
                                Auth.saveKey(key)

                                navController.navigate("menu") {
                                    popUpTo("auth") {
                                        inclusive = true
                                    }
                                }
                            }

                            is ApiResult.Error -> {
                                error = result.message
                            }
                        }
                        loading = false
                    }
                }
            )
        }
    }
}

@Composable
private fun AuthCard(
    key: String,
    error: String,
    loading: Boolean,
    onKeyChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Введите ключ доступа",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            VerticalSpacer(24)

            KeyInput(
                value = key,
                enabled = !loading,
                onValueChange = onKeyChanged
            )

            ErrorBlock(error)

            SubmitBlock(
                visible = !loading && key.isNotBlank(),
                onClick = onSubmit
            )

            LoadingBlock(loading)
        }
    }
}

@Composable
private fun KeyInput(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        textStyle = MaterialTheme.typography.titleLarge,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF2F2F2),
            disabledContainerColor = Color(0xFFE0E0E0),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ErrorBlock(error: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = Color(0xFFFF6B6B),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SubmitBlock(
    visible: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        contentAlignment = Alignment.Center
    ) {
        if (visible) {
            AppButton(
                text = "Проверить",
                onClick = onClick
            )
        }
    }
}

@Composable
private fun LoadingBlock(loading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}
