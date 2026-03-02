package ru.prohor.universe.chopper.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.prohor.universe.chopper.client.ChopperHelper

@RestController
@RequestMapping("/api")
class ChopperController(
    @param:Value($$"${universe.chopper.api-key}")
    private val apiKey: String,
    private val service: ChopperService
) {
    @PostMapping("/message")
    fun sendMessage(
        @RequestHeader(value = ChopperHelper.API_KEY_HEADER, required = false) apiKey: String?,
        @RequestBody body: String,
        @RequestParam(value = ChopperHelper.CHAT_ID) chatId: Long,
        @RequestParam(value = ChopperHelper.MARKDOWN, defaultValue = "false") markdown: Boolean
    ): ResponseEntity<Void> {
        if (apiKey.isInvalid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return service.sendMessage(
            text = body,
            chatId = chatId,
            markdown = markdown
        )
    }

    @PostMapping("/file")
    fun sendFile(
        @RequestHeader(value = ChopperHelper.API_KEY_HEADER, required = false) apiKey: String?,
        @RequestParam(value = ChopperHelper.FILE) file: MultipartFile,
        @RequestParam(value = ChopperHelper.FILE_NAME, required = false) fileName: String?,
        @RequestParam(value = ChopperHelper.CAPTION, required = false) caption: String?,
        @RequestParam(value = ChopperHelper.CHAT_ID) chatId: Long,
        @RequestParam(value = ChopperHelper.CHAT_ID, defaultValue = "false") markdown: Boolean
    ): ResponseEntity<Void> {
        if (apiKey.isInvalid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return service.sendFile(
            file = file,
            fileName = fileName,
            caption = caption,
            chatId = chatId,
            markdown = markdown
        )
    }

    private fun String?.isInvalid(): Boolean {
        return this == null || this != apiKey
    }
}
