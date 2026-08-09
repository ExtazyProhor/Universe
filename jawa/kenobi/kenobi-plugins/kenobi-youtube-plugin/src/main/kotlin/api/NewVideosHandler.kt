package ru.prohor.universe.kenobi.plugin.youtube.api

import ru.prohor.universe.kenobi.plugin.youtube.model.Subscriber
import ru.prohor.universe.kenobi.plugin.youtube.model.VideoInfo

interface NewVideosHandler {
    /**
     * Processes new, not yet processed videos. For example, sends notifications to subscribers
     */
    fun onNewVideos(subscriber: Subscriber, newVideos: List<VideoInfo>)
}
