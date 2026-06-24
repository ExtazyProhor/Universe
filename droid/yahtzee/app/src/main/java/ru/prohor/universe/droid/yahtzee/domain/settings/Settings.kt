package ru.prohor.universe.droid.yahtzee.domain.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val keepTeamsOrderOnShuffle: Boolean = true
)
