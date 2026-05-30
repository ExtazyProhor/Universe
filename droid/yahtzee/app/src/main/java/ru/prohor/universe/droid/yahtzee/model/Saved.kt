package ru.prohor.universe.droid.yahtzee.model

import kotlinx.serialization.Serializable
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Serializable
data class SavedGame(
    val uuid: String = UUID.randomUUID().toString(),
    val finish: Long = Instant.now(Clock.systemUTC()).epochSecond,
    val teams: List<SavedTeam>
) {
    fun description() = GameDescription(uuid, finish, teams.size)
}

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
