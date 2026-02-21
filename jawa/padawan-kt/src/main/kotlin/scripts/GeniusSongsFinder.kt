package ru.prohor.universe.kt.padawan.scripts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.HttpUrl.Companion.toHttpUrl

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

object Constants {
    const val URL = "https://api.genius.com"
    const val TOKEN = ""
    const val JSON_FILE = "padawan/src/main/resources/json.json"
    const val JSON_FILE_2 = "padawan/src/main/resources/json2.json"
}

fun main() {

}

fun findSongsWithLinksToLyrics() {
    val geniusApi = GeniusApi(Constants.URL, Constants.TOKEN)
    for (id in listOf(6676359, 6676350, 7373955)) {
        println("-".repeat(100))
        println(geniusApi.song(id))
        println("-".repeat(100))
    }
}

fun findAllByKish() {
    val geniusApi = GeniusApi(Constants.URL, Constants.TOKEN)
    val mapper = jacksonObjectMapper()

    val artistId = searchArtistId(geniusApi, mapper, "Король и Шут")
    val songs = loadAllArtistSongs(geniusApi, mapper, artistId)

    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(songs)

    File(Constants.JSON_FILE_2).writeText(json)
}

fun findAllKish() {
    val geniusApi = GeniusApi(Constants.URL, Constants.TOKEN)
    val mapper = jacksonObjectMapper()
    val list = searchAll(geniusApi, mapper, "Король и Шут")

    val jsonString: String = mapper.writeValueAsString(list)
    File(Constants.JSON_FILE).writeText(jsonString)
}

fun searchAll(geniusApi: GeniusApi, mapper: ObjectMapper, query: String): List<JsonNode> {
    val result = mutableListOf<JsonNode>()
    var i = 1
    while (true) {
        val response = geniusApi.search(i++, query)
        val root = mapper.readTree(response)

        val items: List<JsonNode> = root["response"]["hits"].elements().asSequence().toList()
        println("page=${i - 1}: size=${items.size}")
        if (items.isEmpty()) {
            break
        }
        result.addAll(items)
    }
    return result
}

fun loadAllArtistSongs(geniusApi: GeniusApi, mapper: ObjectMapper, artistId: Long): List<JsonNode> {
    val songs = mutableListOf<JsonNode>()
    var page = 1

    while (true) {
        val json = geniusApi.artistSongs(artistId, page++)
        val root = mapper.readTree(json)

        val items = root["response"]["songs"]
            .elements()
            .asSequence()
            .toList()

        println("songs page=${page - 1}: ${items.size}")

        if (items.isEmpty()) break

        songs += items
    }

    return songs
}

fun searchArtistId(geniusApi: GeniusApi, mapper: ObjectMapper, name: String): Long {
    val json = geniusApi.search(1, name)
    val root = mapper.readTree(json)

    return root["response"]["hits"]
        .map { it["result"]["primary_artist"] }
        .first { it["name"].asText().lowercase().contains(name.lowercase()) }["id"]
        .asLong()
}

class GeniusApi(
    baseUrl: String,
    accessToken: String
) {
    private val baseUrl = baseUrl.toHttpUrl()
    private val authHeader = "Bearer $accessToken"
    private val client = OkHttpClient()

    fun song(id: Int): String {
        val url = baseUrl.newBuilder()
            .addPathSegment("songs")
            .addPathSegment(id.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addToken()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("$response")
            }
            val body = response.body?.string() ?: throw RuntimeException("empty body in response: $response")
            return body
        }
    }

    fun search(page: Int, query: String): String {
        val url = baseUrl.newBuilder()
            .addPathSegment("search")
            .addQueryParameter("q", query)
            .addQueryParameter("per_page", "20")
            .addQueryParameter("page", page.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addToken()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("$response")
            }
            val body = response.body?.string() ?: throw RuntimeException("empty body in response: $response")
            return body
        }
    }

    fun artistSongs(artistId: Long, page: Int): String {
        val url = baseUrl.newBuilder()
            .addPathSegment("artists")
            .addPathSegment(artistId.toString())
            .addPathSegment("songs")
            .addQueryParameter("per_page", "50")
            .addQueryParameter("page", page.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addToken()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("$response")
            }
            val body = response.body?.string() ?: throw RuntimeException("empty body in response: $response")
            return body
        }
    }

    private fun Request.Builder.addToken(): Request.Builder {
        return addHeader("Authorization", authHeader)
    }
}
