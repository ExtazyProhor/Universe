package ru.prohor.universe.chopper.client

import java.io.File

interface ChopperClient {
    fun sendMessage(
        text: String,
        chatId: Long,
        markdown: Boolean = false
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
