package ru.prohor.universe.droid.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.prohor.universe.droid.yahtzee.auth.Auth
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGamesState
import ru.prohor.universe.droid.yahtzee.domain.team.TeamTemplatesState
import ru.prohor.universe.droid.yahtzee.mocks.Mocks
import ru.prohor.universe.droid.yahtzee.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        Auth.initialize(this)
        SavedGamesState.initialize(this)
        TeamTemplatesState.initialize(this)
        Mocks.initGames(this)
        Mocks.initTeams()

        setContent {
            AppNavigation()
        }
    }
}
