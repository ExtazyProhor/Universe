package ru.prohor.universe.kenobi.plugin.youtube.model

import org.bson.types.ObjectId

data class Subscriber(
    val id: ObjectId,
    val name: String,
    val telegramChatId: Long,
    val enabled: Boolean,
    val channels: List<ChannelInfo>,
    val processedVideos: List<VideoInfo>,
)

data class ChannelInfo(
    val link: String,
    val name: String? = null,
    val channelId: String? = null
)
