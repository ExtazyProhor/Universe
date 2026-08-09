package ru.prohor.universe.kenobi.plugin.youtube.api

import ru.prohor.universe.kenobi.plugin.youtube.model.ChannelInfo

interface ChannelInfoProvider {
    /**
     * Finds channel name and channel id (like `UC**********************`)
     * by channel link (like `https://www.youtube.com/@***`)
     */
    fun getChannelInfo(channelLink: String): ChannelInfo
}
