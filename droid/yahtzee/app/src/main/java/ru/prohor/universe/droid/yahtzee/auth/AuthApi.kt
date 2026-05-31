package ru.prohor.universe.droid.yahtzee.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.prohor.universe.droid.yahtzee.BuildConfig
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

object AuthApi {
    private const val HOST = BuildConfig.API_URL
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    suspend fun validate(key: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$HOST/mobile/auth/validate")
                    .header("X-User-Key", key)
                    .build()
                val response = client.newCall(request).execute()

                when (response.code) {
                    200 -> AuthResult.Success
                    403 -> AuthResult.Error("Неверный ключ")
                    else -> AuthResult.Error("Ошибка сервера (${response.code})")
                }
            } catch (_: SocketTimeoutException) {
                AuthResult.Error("Превышено время ожидания")
            } catch (_: UnknownHostException) {
                AuthResult.Error("Нет соединения с сервером")
            } catch (_: ConnectException) {
                AuthResult.Error("Ошибка подключения к серверу")
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
