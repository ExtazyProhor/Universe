package ru.prohor.universe.kenobi.plugin.youtube.impl

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import ru.prohor.universe.jocasta.core.string.MarkdownV2
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor
import ru.prohor.universe.kenobi.plugin.youtube.api.NewVideosHandler
import ru.prohor.universe.kenobi.plugin.youtube.model.Subscriber
import ru.prohor.universe.kenobi.plugin.youtube.model.VideoInfo
import java.time.Duration
import java.time.Instant

@Service
class TelegramBotNewVideosHandler(
    private val feedbackExecutor: FeedbackExecutor
) : NewVideosHandler {
    override fun onNewVideos(subscriber: Subscriber, newVideos: List<VideoInfo>) {
        val minTime = Instant.now().toEpochMilli() - Duration.ofDays(1).toMillis()
        newVideos.filter { it.published > minTime }.sortedBy { it.published }.forEach {
            sendVideo(feedbackExecutor, subscriber.telegramChatId, it)
        }
    }

    private fun sendVideo(feedbackExecutor: FeedbackExecutor, chatId: Long, video: VideoInfo) {
        var text = MarkdownV2()
            .text("🎬 Новое видео!")
            .newline(2)
            .bold {
                link(text = video.title, url = video.link)
            }
            .newline(2)
            .text("Авторы: ")

        val authors = video.authors
        authors.forEachIndexed { index, author ->
            text = text.link(text = author.name, url = author.link)
            if (index < authors.size - 1) {
                text = text.text(", ")
            }
        }

        feedbackExecutor.sendPhoto(
            chatId,
            InputFile(video.thumbnail),
            text.toString(),
            ParseMode.MARKDOWNV2
        )
    }
}
