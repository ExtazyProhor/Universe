package ru.prohor.universe.fulcrum

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

@RestController
@RequestMapping("/api/content-list")
class DirectoryContentController(
    @Value($$"${universe.fulcrum.content-dir}") contentDirectory: String,
) {
    private val contentDirectory = Path(contentDirectory)
    private val objectMapper = jacksonObjectMapper()

    @GetMapping
    fun listFiles(
        @RequestParam(name = "path", required = false, defaultValue = "") path: String
    ): ResponseEntity<List<FileEntryDto>> {
        if (Path(path).isAbsolute) {
            return ResponseEntity.badRequest().build()
        }
        val resolvedPath = contentDirectory.resolve(path).normalize()

        if (!resolvedPath.startsWith(contentDirectory)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        if (!Files.exists(resolvedPath)) {
            Files.createDirectories(resolvedPath)
        }
        if (!Files.isDirectory(resolvedPath)) {
            return ResponseEntity.notFound().build()
        }

        val fileEntities = try {
            readFileEntities(resolvedPath)
        } catch (_: IOException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return ResponseEntity.ok(fileEntities)
    }

    private fun readFileEntities(resolvedPath: Path): List<FileEntryDto> {
        val config = readConfig(resolvedPath)
        return Files.newDirectoryStream(resolvedPath).use { stream ->
            stream
                .filterNot { isIgnored(it) }
                .filterNot { it.fileName.toString() == "config.json" }
                .map { toFileEntryDto(it, config) }
                .sortedWith(compareBy({ it.type }, { it.name }))
                .toList()
        }
    }

    private fun isIgnored(path: Path): Boolean {
        return path.fileName.toString().startsWith('.')
    }

    private fun toFileEntryDto(
        entry: Path,
        config: Map<String, String>
    ) = FileEntryDto(
        name = if (Files.isDirectory(entry)) {
            entry.fileName.toString()
        } else {
            config[entry.fileName.toString()] ?: entry.fileName.toString()
        },
        file = entry.fileName.toString(),
        type = if (Files.isDirectory(entry)) {
            EntryType.DIRECTORY
        } else {
            EntryType.FILE
        }
    )

    private fun readConfig(dir: Path): Map<String, String> {
        val configPath = dir.resolve("config.json")

        if (!Files.exists(configPath)) return emptyMap()

        return try {
            Files.newBufferedReader(configPath).use { reader ->
                val list: List<FileConfigEntry> =
                    objectMapper.readValue(reader, object : TypeReference<List<FileConfigEntry>>() {})
                list.associate { it.file to it.name }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    data class FileConfigEntry(
        val name: String,
        val file: String
    )

    data class FileEntryDto(
        val name: String,
        val file: String,
        val type: EntryType
    )

    enum class EntryType {
        @JsonProperty("directory")
        DIRECTORY,

        @JsonProperty("file")
        FILE
    }
}
