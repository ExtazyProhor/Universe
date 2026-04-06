package ru.prohor.universe.kt.padawan.spring.musicmvp

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils

@RestController
@RequestMapping("/api")
class TrackController(
    private val repo: TrackRepository
) {
    @GetMapping("/tracks")
    fun getTracks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): List<Track> {
        val from = page * size
        val to = minOf(from + size, repo.tracks.size)

        if (from >= repo.tracks.size)
            return emptyList()
        return repo.tracks.subList(from, to)
    }

    @GetMapping("/tracks/{filename}")
    fun getTrack(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = FileSystemUtils.userHome().asPath().resolve("Downloads/music/$filename").toFile()
        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok()
            .header("Content-Type", "audio/mpeg")
            .body(FileSystemResource(file))
    }
}
