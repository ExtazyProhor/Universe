package ru.prohor.universe.droid.yahtzee.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.prohor.universe.droid.yahtzee.BuildConfig
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

object YahtzeeApi {
    private const val USER_KEY_HEADER = "X-User-Key"
    private const val HOST = BuildConfig.API_URL
    private val mediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    suspend fun validateKey(userKey: String): ApiResult {
        return call(
            path = "/mobile/validate",
            userKey = userKey
        )
    }

    suspend fun sendGame(game: String, userKey: String): ApiResult {
        return call(
            path = "/mobile/game",
            userKey = userKey,
            json = game
        )
    }

    private suspend fun call(
        path: String,
        userKey: String,
        json: String? = null
    ): ApiResult {
        val body = json?.toRequestBody(mediaType)
        val url = "$HOST$path"
        val request = Request.Builder()
            .url(url)
            .header(USER_KEY_HEADER, userKey)
            .let {
                if (body != null) it.post(body) else it
            }
            .build()

        return call(request)
    }

    private suspend fun call(request: Request): ApiResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                when (response.code) {
                    200 -> ApiResult.Success
                    403 -> ApiResult.Error("Неверный ключ")
                    else -> ApiResult.Error("Ошибка сервера (${response.code})")
                }
            } catch (_: SocketTimeoutException) {
                ApiResult.Error("Превышено время ожидания")
            } catch (_: UnknownHostException) {
                ApiResult.Error("Нет соединения с сервером")
            } catch (_: ConnectException) {
                ApiResult.Error("Ошибка подключения к серверу")
            } catch (e: Exception) {
                ApiResult.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
