package ru.prohor.universe.kt.padawan.spring.musicmvp

data class Track(
    val name: String,
    val authors: List<String>,
    val track: String,
    val duration: Double
)

data class EnrichedTrack(
    val name: String,
    val authors: List<String>,
    val track: String,
    val duration: Double,

    val title: String?,
    val artist: String?,
    val album: String?,

    val cover: String?,
    val coverWidth: Int?,
    val coverHeight: Int?,

    val meta: Map<String, Any>
)
