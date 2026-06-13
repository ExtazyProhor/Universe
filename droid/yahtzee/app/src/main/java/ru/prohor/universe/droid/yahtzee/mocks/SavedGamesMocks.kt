package ru.prohor.universe.droid.yahtzee.mocks

import android.content.Context
import ru.prohor.universe.droid.yahtzee.domain.game.ALL_COMBINATIONS
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedCombination
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGame
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedGamesState
import ru.prohor.universe.droid.yahtzee.domain.storage.SavedTeam

object SavedGamesMocks {
    fun games(count: Int, context: Context) {
        repeat(count) {
            game((1..6).random(), context)
        }
    }

    fun game(teams: Int, context: Context) {
        val game = SavedGame(
            teams = List(teams) { index ->
                SavedTeam(
                    name = TeamsMocks.generateTeamName(index),
                    scores = ALL_COMBINATIONS.map {
                        SavedCombination(
                            combination = it.name,
                            value = CombinationsMocks.generateFor(it)
                        )
                    }
                )
            }
        )
        SavedGamesState.save(game, context)
    }
}
