package ru.prohor.universe.droid.yahtzee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.prohor.universe.droid.yahtzee.screens.GameScreen
import ru.prohor.universe.droid.yahtzee.screens.MainMenuScreen
import ru.prohor.universe.droid.yahtzee.screens.MyGamesScreen
import ru.prohor.universe.droid.yahtzee.screens.NewGameScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
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
            GameScreen()
        }
    }
}
