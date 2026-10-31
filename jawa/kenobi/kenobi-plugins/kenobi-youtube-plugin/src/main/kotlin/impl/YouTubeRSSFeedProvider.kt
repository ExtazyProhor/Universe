package ru.prohor.universe.kenobi.plugin.youtube.impl

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import ru.prohor.universe.kenobi.plugin.youtube.api.LatestChannelVideosProvider
import ru.prohor.universe.kenobi.plugin.youtube.model.AuthorInfo
import ru.prohor.universe.kenobi.plugin.youtube.model.VideoInfo
import java.io.ByteArrayInputStream
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

@Service
class YouTubeRSSFeedProvider : LatestChannelVideosProvider {
    private val client = OkHttpClient.Builder()
        .callTimeout(Duration.ofSeconds(10))
        .retryOnConnectionFailure(true)
        .build()

    override fun getLatestChannelVideos(channelId: String): List<VideoInfo> {
        val url = "https://www.youtube.com/feeds/videos.xml?channel_id=$channelId"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // TODO log
                System.err.println(
                    "${LocalDateTime.now()} failed to get RSS. ChannelId: '$channelId', code: ${response.code}"
                )
                return emptyList()
            }
            val bytes = response.body?.bytes() ?: error("empty body")
            val feed = SyndFeedInput().build(XmlReader(ByteArrayInputStream(bytes)))
            val receivedAt = Instant.now().toEpochMilli()

            val list = feed.entries.map { entry ->
                val videoId = entry.uri.substringAfterLast(":")
                VideoInfo(
                    videoId = videoId,
                    channelId = channelId,
                    title = entry.title,
                    link = entry.link,
                    authors = entry.authors.map { author ->
                        AuthorInfo(
                            name = author.name,
                            link = author.uri
                        )
                    },
                    published = entry.publishedDate?.time ?: 0,
                    thumbnail = "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg",
                    receivedAt = receivedAt
                )
            }
            return list
        }
    }
}
