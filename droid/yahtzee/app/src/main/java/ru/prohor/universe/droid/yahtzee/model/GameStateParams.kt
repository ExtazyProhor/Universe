package ru.prohor.universe.droid.yahtzee.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GameStateParams {
    var currentTeamIndex by mutableIntStateOf(0)
    var lastCombination by mutableStateOf<Combination?>(null)
    var gameFinished by mutableStateOf(false)
    var saved = false
}
