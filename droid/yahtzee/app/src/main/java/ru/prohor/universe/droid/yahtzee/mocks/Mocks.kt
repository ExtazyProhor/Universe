package ru.prohor.universe.droid.yahtzee.mocks

import android.content.Context

object Mocks {
    fun initGames(context: Context) {

    }

    fun initTeams() {

    }

    private var scoresInitiated = false

    fun initScoresOnce() {
        if (scoresInitiated) return
        scoresInitiated = true

    }

    fun initScores() {

    }
}
