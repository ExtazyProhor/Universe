package ru.prohor.universe.padawan.kotlin.spring.mvpplayer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils
import ru.prohor.universe.jocasta.morphia.impl.MongoFileRepository
import ru.prohor.universe.jocasta.morphia.jackson.JacksonMorphiaConfiguration
import java.awt.Desktop
import java.io.File
import java.net.URI

private val COLLECTION_FILE = FileSystemUtils.userHome()
    .resolve("my")
    .resolve("music-collection.json")
    .asString()

private val LIBRARY_DIR = FileSystemUtils.downloads()
    .resolve("export")
    .resolve("features")
    .resolve("music-player")
    .asString()
private const val PORT = 8080

@SpringBootApplication
class MusicPlayerMvp

fun main(vararg args: String) {
    runApplication<MusicPlayerMvp>(*args)
}

data class Track(
    val title: String,
    val authors: List<String>,
    val album: String?,
    val track: String,
    val cover: String?,
    val duration: Double?,
)

data class Queue(
    val id: ObjectId,
    val tracks: List<String>,
    val currentTrackId: String
)

val repository = MongoFileRepository(
    { it.id },
    Queue::class.java,
    COLLECTION_FILE,
    jacksonObjectMapper().registerModule(JacksonMorphiaConfiguration.createMorphiaModule())
)

private val QUEUE_ID = ObjectId("000000000000000000000000")

data class QueueResponse(
    val tracks: List<Track>,
    val currentTrackId: String,
)

data class CurrentTrackRequest(val trackId: String)

@RestController
class QueueController(private val library: TrackLibrary) {
    @GetMapping("/api/queue")
    fun get(): QueueResponse {
        val saved = repository.findAll().firstOrNull { it.id == QUEUE_ID }
        val queue = saved ?: shuffledQueue().also { repository.save(it) }
        return queue.toResponse()
    }

    @PostMapping("/api/queue/shuffle")
    fun shuffle(): QueueResponse {
        val queue = shuffledQueue()
        repository.save(queue)
        return queue.toResponse()
    }

    @PostMapping("/api/queue/current")
    fun setCurrent(@RequestBody body: CurrentTrackRequest): QueueResponse {
        val saved = repository.findAll().firstOrNull { it.id == QUEUE_ID } ?: shuffledQueue()
        val updated = saved.copy(currentTrackId = body.trackId)
        repository.save(updated)
        return updated.toResponse()
    }

    private fun shuffledQueue(): Queue {
        val order = library.tracks.map { it.track }.shuffled()
        return Queue(id = QUEUE_ID, tracks = order, currentTrackId = order.first())
    }

    private fun Queue.toResponse() = QueueResponse(
        tracks = tracks.mapNotNull { library.byTrackId(it) },
        currentTrackId = currentTrackId,
    )
}

@Component
class TrackLibrary(mapper: ObjectMapper) {
    val tracks: List<Track> = loadTracks(mapper)

    fun byTrackId(id: String): Track? = tracks.firstOrNull { it.track == id }
}

@RestController
class TracksController(private val library: TrackLibrary) {

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): Resource = ClassPathResource("music-player-mvp.html")

    @GetMapping("/api/tracks")
    fun list(): List<Track> = library.tracks
}

@Configuration
class StaticFilesConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/music/**").addResourceLocations(dirUrl("music"))
        registry.addResourceHandler("/cover/**").addResourceLocations(dirUrl("cover"))
    }
}

@Component
class BrowserLauncher {
    @EventListener(ApplicationReadyEvent::class)
    fun open() {
        println("Плеер: http://127.0.0.1:$PORT  (папка: $LIBRARY_DIR)")
        runCatching {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(URI("http://127.0.0.1:$PORT"))
        }
    }
}

private fun dirUrl(name: String): String = File(LIBRARY_DIR, name).toURI().toString().let {
    if (it.endsWith("/")) it else "$it/"
}

private fun JsonNode.textOrNull(): String? = if (isNull || isMissingNode) null else asText()

private fun loadTracks(mapper: ObjectMapper): List<Track> {
    val file = File(LIBRARY_DIR, "all_lyrics.json")
    require(file.exists()) { "Не найден ${file.absolutePath} - проверьте LIBRARY_DIR в MusicPlayerMvp.kt" }

    return mapper.readTree(file).mapNotNull { n ->
        val track = n.path("track").textOrNull() ?: return@mapNotNull null
        val meta = n.path("meta")
        val authors = n.path("authors").mapNotNull { it.textOrNull() }
            .ifEmpty { listOfNotNull(meta.path("artist").textOrNull()) }
        Track(
            title = n.path("name").textOrNull() ?: meta.path("title").textOrNull() ?: track,
            authors = authors,
            album = meta.path("album").textOrNull(),
            track = track,
            cover = n.path("cover").path("file").textOrNull(),
            duration = n.path("duration").takeIf { it.isNumber }?.asDouble(),
        )
    }
}
