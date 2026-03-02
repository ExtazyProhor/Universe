package ru.prohor.universe.chopper.app

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.File

@Service
class ChopperService(bot: ChopperBot) {
    private val feedbackExecutor = bot.feedbackExecutor

    fun sendMessage(
        text: String,
        chatId: Long,
        markdown: Boolean
    ): ResponseEntity<Void> {
        val message = SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .apply {
                if (markdown) parseMode(ParseMode.MARKDOWNV2)
            }
            .build()

        feedbackExecutor.sendMessage(message)
        return ResponseEntity.ok().build()
    }

    fun sendFile(
        file: MultipartFile,
        fileName: String?,
        caption: String?,
        chatId: Long,
        markdown: Boolean
    ): ResponseEntity<Void> {
        val temp = File.createTempFile("upload", file.originalFilename)
        file.transferTo(temp)
        val file = (fileName ?: file.originalFilename)?.let { InputFile(temp, it) } ?: InputFile(temp)

        val document = SendDocument.builder()
            .chatId(chatId)
            .document(file)
            .apply {
                caption?.let {
                    caption(it)
                    if (markdown) {
                        parseMode(ParseMode.MARKDOWNV2)
                    }
                }
            }
            .build()

        feedbackExecutor.sendDocument(document)
        temp.delete()
        return ResponseEntity.ok().build()
    }
}
