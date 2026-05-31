package ru.prohor.universe.droid.yahtzee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.mocks.MockScreen
import ru.prohor.universe.droid.yahtzee.screens.AuthScreen
import ru.prohor.universe.droid.yahtzee.screens.FinishScreen
import ru.prohor.universe.droid.yahtzee.screens.GameScreen
import ru.prohor.universe.droid.yahtzee.screens.MainMenuScreen
import ru.prohor.universe.droid.yahtzee.screens.MyGamesScreen
import ru.prohor.universe.droid.yahtzee.screens.NewGameScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (Auth.hasKey()) "menu" else "auth"
    ) {
        composable("auth") {
            AuthScreen(navController)
        }

        composable("menu") {
            MainMenuScreen(navController)
        }

        composable("new_game") {
            NewGameScreen(navController)
        }

        composable("my_games") {
            MyGamesScreen(navController)
        }

        composable("game") {
            GameScreen(navController)
        }

        composable("finish") {
            FinishScreen(navController)
        }

        composable("mock") {
            MockScreen()
        }
    }
}
