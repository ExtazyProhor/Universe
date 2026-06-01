package ru.prohor.universe.droid.yahtzee.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.mocks.MockScreen
import ru.prohor.universe.droid.yahtzee.screens.AuthScreen
import ru.prohor.universe.droid.yahtzee.screens.FinishScreen
import ru.prohor.universe.droid.yahtzee.screens.MainMenuScreen
import ru.prohor.universe.droid.yahtzee.screens.MyGamesScreen
import ru.prohor.universe.droid.yahtzee.screens.NewGameScreen
import ru.prohor.universe.droid.yahtzee.screens.game.GameScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (Auth.hasKey()) "menu" else "auth"
    ) {
        composableWithAnimation("auth") {
            AuthScreen(navController)
        }

        composableWithAnimation("menu") {
            MainMenuScreen(navController)
        }

        composableWithAnimation("new_game") {
            NewGameScreen(navController)
        }

        composableWithAnimation("my_games") {
            MyGamesScreen(navController)
        }

        composableWithAnimation("game") {
            GameScreen(navController)
        }

        composableWithAnimation("finish") {
            FinishScreen(navController)
        }

        composableWithAnimation("mock") {
            MockScreen()
        }
    }
}

private fun NavGraphBuilder.composableWithAnimation(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut()
        },
        content = content
    )
}
