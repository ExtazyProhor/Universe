package ru.prohor.universe.droid.yahtzee.mocks

import android.content.Context
import androidx.navigation.NavController
import ru.prohor.universe.droid.yahtzee.navigation.NavigationActions

object Mocks {
    private var activated = false

    fun activate(navController: NavController, context: Context) {
        if (activated) return
        activated = true
        activateOnce(navController, context)
    }

    fun activateOnce(navController: NavController, context: Context) {

    }

    fun startGame(navController: NavController, context: Context) {
        NavigationActions.newGame(navController)
        TeamsMocks.generateTeams(1)
        NavigationActions.startGame(navController, context)
    }
}
