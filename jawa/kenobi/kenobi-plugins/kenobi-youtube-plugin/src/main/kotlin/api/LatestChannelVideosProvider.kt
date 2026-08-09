package ru.prohor.universe.kenobi.plugin.youtube.api

import ru.prohor.universe.kenobi.plugin.youtube.model.VideoInfo

interface LatestChannelVideosProvider {
    /**
     * Loads information about the latest videos from the channel with [channelId]
     */
    fun getLatestChannelVideos(channelId: String): List<VideoInfo>
}
