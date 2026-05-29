package ru.prohor.universe.droid.yahtzee.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.R
import ru.prohor.universe.droid.yahtzee.ui.shared.AppButton
import ru.prohor.universe.droid.yahtzee.ui.shared.VerticalSpacer

@Composable
fun MainMenuScreen(navController: NavController) {
    val activity = LocalActivity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppButton(
                text = "Новая игра",
                modifier = Modifier.fillMaxWidth(0.55f),
                onClick = { navController.navigate("new_game") }
            )

            VerticalSpacer(20)

            AppButton(
                text = "Мои игры",
                modifier = Modifier.fillMaxWidth(0.55f),
                onClick = { navController.navigate("my_games") }
            )
        }

        AppButton(
            text = "Выход",
            onClick = { activity?.finish() },
            containerColor = Color(0xFF8B0000),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}
