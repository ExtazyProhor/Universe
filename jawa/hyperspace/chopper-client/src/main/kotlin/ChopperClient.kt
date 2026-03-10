package ru.prohor.universe.chopper.client

import java.io.File

interface ChopperClient {
    /**
     * This is needed for java code, since [@JvmOverloads][JvmOverloads] does not work on interfaces
     */
    fun sendMessage(
        text: String,
        chatId: Long
    ): Boolean {
        return sendMessage(
            text = text,
            chatId = chatId,
            markdown = false,
            disableLinkPreview = false
        )
    }

    fun sendMessage(
        text: String,
        chatId: Long,
        markdown: Boolean = false,
        disableLinkPreview: Boolean = false
    ): Boolean

    fun sendFile(
        file: File,
        chatId: Long,
        fileName: String? = null,
        caption: String? = null,
        markdown: Boolean = false
    ): Boolean

    fun sendFile(
        content: String,
        chatId: Long,
        fileName: String? = null,
        caption: String? = null,
        markdown: Boolean = false
    ): Boolean
}
