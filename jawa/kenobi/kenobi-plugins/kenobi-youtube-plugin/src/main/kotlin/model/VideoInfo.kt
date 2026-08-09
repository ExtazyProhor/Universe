package ru.prohor.universe.kenobi.plugin.youtube.model

data class VideoInfo(
    val videoId: String,
    val channelId: String,
    val title: String,
    val link: String,
    val authors: List<AuthorInfo>,
    val published: Long,
    val thumbnail: String,
    val receivedAt: Long
)

data class AuthorInfo(
    val name: String,
    val link: String
)
