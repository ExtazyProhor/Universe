package ru.prohor.universe.chopper.client

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ChopperOkHttpClient(
    private val client: OkHttpClient,
    private val baseUrl: String,
    private val apiKey: String
) : ChopperClient {

    override fun sendMessage(
        text: String,
        chatId: Long,
        markdown: Boolean
    ): Boolean {
        val url = baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegment("message")
            .addQueryParameter(ChopperHelper.CHAT_ID, chatId.toString())
            .addQueryParameter(ChopperHelper.MARKDOWN, markdown.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader(ChopperHelper.API_KEY_HEADER, apiKey)
            .post(text.toRequestBody())
            .build()

        return execute(request)
    }

    override fun sendFile(
        file: File,
        chatId: Long,
        fileName: String?,
        caption: String?,
        markdown: Boolean
    ): Boolean {
        return sendFile(
            requestBody = file.asRequestBody(),
            chatId = chatId,
            originalFileName = file.name,
            fileName = fileName,
            caption = caption,
            markdown = markdown
        )
    }

    override fun sendFile(
        content: String,
        chatId: Long,
        fileName: String?,
        caption: String?,
        markdown: Boolean
    ): Boolean {
        return sendFile(
            requestBody = content.toRequestBody("text/plain".toMediaType()),
            chatId = chatId,
            originalFileName = fileName ?: "file.txt",
            fileName = fileName,
            caption = caption,
            markdown = markdown
        )
    }

    private fun sendFile(
        requestBody: RequestBody,
        chatId: Long,
        originalFileName: String,
        fileName: String?,
        caption: String?,
        markdown: Boolean
    ): Boolean {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(ChopperHelper.FILE, originalFileName, requestBody)
            .addFormDataPart(ChopperHelper.CHAT_ID, chatId.toString())
            .addFormDataPart(ChopperHelper.MARKDOWN, markdown.toString())
            .apply {
                caption?.let { addFormDataPart(ChopperHelper.CAPTION, it) }
                fileName?.let { addFormDataPart(ChopperHelper.FILE_NAME, it) }
            }
            .build()

        val url = baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegment("file")
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader(ChopperHelper.API_KEY_HEADER, apiKey)
            .post(body)
            .build()

        return execute(request)
    }

    private fun execute(request: Request): Boolean {
        return runCatching {
            // TODO verbose log
            client.newCall(request)
                .execute()
                .use { response ->
                    val isSuccess = response.isSuccessful
                    if (!isSuccess) {
                        // TODO log
                        println("Request failed: ${request.url}, code: ${response.code}, message: ${response.message}")
                    }
                    isSuccess
                }
        }.onFailure { exception ->
            println("Network exception for ${request.url}: ${exception.message}")
            exception.printStackTrace()
        }.getOrDefault(false)
    }
}
