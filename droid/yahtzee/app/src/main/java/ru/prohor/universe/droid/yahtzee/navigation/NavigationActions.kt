package ru.prohor.universe.droid.yahtzee.navigation

import android.content.Context
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.domain.game.GameState
import ru.prohor.universe.droid.yahtzee.domain.team.TeamTemplatesState
import ru.prohor.universe.droid.yahtzee.domain.team.TeamsState

object NavigationActions {
    fun newGame(navController: NavController) {
        navController.navigateSingle("new_game")
    }

    fun myGames(navController: NavController) {
        navController.navigateSingle("my_games")
    }

    fun startGame(navController: NavController, context: Context) {
        if (!TeamsState.isAvailableToStartGame()) return
        GameState.initialize()
        TeamTemplatesState.registerTemplates(context)
        navController.navigateSingle("game")
    }

    fun finishGame(navController: NavController, context: Context) {
        GameState.saveGame(context)

        navController.navigateSingle("finish") {
            popUpTo("game") {
                inclusive = true
            }
        }
    }

    fun back(navController: NavController) {
        navController.popBackStack()
    }
}
