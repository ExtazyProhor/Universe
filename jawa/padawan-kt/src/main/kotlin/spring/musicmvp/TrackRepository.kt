package ru.prohor.universe.kt.padawan.spring.musicmvp

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import kotlin.io.path.Path
import kotlin.io.path.readText


@Service
class TrackRepository {
    private val objectMapper = jacksonObjectMapper()

    val tracks: List<Track> = load()

    private fun load(): List<Track> {
        return objectMapper.readValue(
            Path("padawan/src/main/resources/json.json").readText(),
            object : TypeReference<List<Track>>() {}
        ).reversed()
    }
}