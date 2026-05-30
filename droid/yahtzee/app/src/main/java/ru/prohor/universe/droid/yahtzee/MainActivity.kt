package ru.prohor.universe.droid.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.prohor.universe.droid.yahtzee.navigation.AppNavigation
import ru.prohor.universe.droid.yahtzee.state.SavedGamesState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavedGamesState.initialize(this)
            Mocks.initTeams()
            AppNavigation()
        }
    }
}
