package ru.prohor.universe.droid.yahtzee.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedGame(
    val uuid: String,
    val finish: Long,
    val teams: List<SavedTeam>
)

@Serializable
data class SavedTeam(
    val name: String,
    val scores: List<SavedCombination>
)

@Serializable
data class SavedCombination(
    val combination: String,
    val value: Int
)

@Serializable
data class GamesDescription(
    val games: List<GameDescription> = emptyList()
)

@Serializable
data class GameDescription(
    val uuid: String,
    val finish: Long,
    val teams: Int
)
