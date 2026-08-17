package ru.prohor.universe.kenobi.plugin.youtube.impl

import org.springframework.stereotype.Service
import ru.prohor.universe.kenobi.plugin.youtube.api.ChannelInfoProvider
import ru.prohor.universe.kenobi.plugin.youtube.model.ChannelInfo

@Service
class YtDlpChannelInfoProvider : ChannelInfoProvider {
    // TODO add test
    private val baseCommand = listOf(
        "yt-dlp",
        "--playlist-items",
        "1",
        "--print",
        "%(channel_id)s|%(channel)s",
    )

    override fun getChannelInfo(channelLink: String): ChannelInfo {
        val process = ProcessBuilder(baseCommand.plus(channelLink)).redirectErrorStream(true).start()
        val result = process.inputStream
            .bufferedReader()
            .readText()
            .trim()
        process.waitFor()

        require(result.isNotBlank()) {
            "Cannot get channel info for $channelLink"
        }
        val parts = result.split("|")
        return ChannelInfo(
            link = channelLink,
            name = parts[1],
            channelId = parts[0]
        )
    }
}
