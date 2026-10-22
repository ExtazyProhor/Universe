package ru.prohor.universe.padawan.kotlin.spring.musicmvp

import com.fasterxml.jackson.module.kotlin.readValue
import org.bson.types.ObjectId
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils
import ru.prohor.universe.padawan.Padawan
import ru.prohor.universe.padawan.PadawanKt
import ru.prohor.universe.padawan.TestFile
import java.io.File

val music = FileSystemUtils.downloads().resolve("mus/test/music").asFile()
val cover = FileSystemUtils.downloads().resolve("mus/test/cover").asFile()

/**
 * track names to mp3 file names
 */
val replacements: Map<String, String> = mapOf()

fun main() {
    addNewTracks()
}

fun addNewTracks() {
    cover.mkdirs()
    val renames = mutableMapOf<File, File>()
    val errors = mutableListOf<String>()
    val unusedTracks = music.list()?.toMutableSet() ?: mutableSetOf()

    val tracks = Padawan.readLines(TestFile.TXT).reversed().map {
        val fileName = "$it.mp3"
        val file = music.resolve(replacements[fileName] ?: fileName)
        if (!file.exists()) {
            errors.add("file not found: $it.mp3")
            return@map Track.EMPTY
        }
        unusedTracks.remove(file.name)
        if (it.substringAfterLast(" - ") != it.substringAfter(" - ")) {
            errors.add("There are multiple substrings ' - ' in name: '$it'")
            return@map Track.EMPTY
        }
        val title = it.substringAfter(" - ")
        val authors = it.substringBefore(" - ").split(", ").flatMap { part -> part.split(" feat. ") }
        val duration = Ffmpeg.getDuration(file)
        val meta = Ffmpeg.extractMetadata(file).map()
        val id = ObjectId()
        val renamed = music.resolve("$id.mp3")
        renames[file] = renamed

        val cover = getCover(file, "$id", cover)

        Track(
            name = title,
            authors = authors,
            track = renamed.name,
            duration = duration,
            cover = cover,
            meta = meta,
            lyrics = Lyrics(null, null) // TODO python
        )
    }
    if (unusedTracks.isNotEmpty()) {
        System.err.println("==============")
        unusedTracks.forEach { System.err.println("unused file: $it") }
        System.err.println("==============")
    }
    if (errors.isNotEmpty()) {
        errors.forEach { System.err.println(it) }
        throw RuntimeException()
    }
    renames.forEach { (file, renamed) ->
        file.renameTo(renamed)
        Ffmpeg.clearMeta(renamed)
    }
    PadawanKt.Jackson.writeList(tracks, TestFile.JSON)
}

fun getCover(input: File, id: String, outputDir: File): Cover? {
    val output = outputDir.resolve("$id.jpg")
    if (!Ffmpeg.extractCover(input, output)) return null
    val (x, y) = Ffmpeg.getImageSize(output) ?: Pair(null, null)
    if (x == null || y == null) return null
    return Cover(output.name, x, y)
}

fun RawMetadataV2.map(): RawMetadata {
    return RawMetadata(
        duration = this.format.duration,
        size = this.format.size,
        title = this.format.tags?.title,
        artist = this.format.tags?.artist,
        album = this.format.tags?.album,
        lyrics = this.format.tags?.lyricsFinderLyrics ?: this.format.tags?.vkNextLyrics,
        genre = this.format.tags?.genre,
        date = this.format.tags?.date,
    )
}

object Ffmpeg {
    fun extractMetadata(file: File): RawMetadataV2 {
        val json = runCommand(
            "ffprobe",
            "-v", "quiet",
            "-print_format", "json",
            "-show_format",
            file.absolutePath
        )
        return PadawanKt.Jackson.mapper.readValue<RawMetadataV2>(json)
    }

    fun clearMeta(file: File) {
        runCommand(
            "id3v2",
            "-D",
            file.absolutePath
        )
    }

    fun extractCover(input: File, output: File): Boolean {
        runCommand(
            "ffmpeg",
            "-y",
            "-i", input.absolutePath,
            "-an",
            "-vcodec", "copy",
            output.absolutePath
        )
        return output.exists() && output.length() > 0
    }

    fun getImageSize(file: File): Pair<Int, Int>? {
        if (!file.exists())
            return null

        val out = runCommand(
            "ffprobe",
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "stream=width,height",
            "-of", "csv=s=x:p=0",
            file.absolutePath
        )

        val parts = out.split("x")
        return if (parts.size == 2) {
            parts[0].toInt() to parts[1].toInt()
        } else null
    }

    // use it from meta
    fun getDuration(file: File): Double {
        return runCommand(
            "ffprobe",
            "-i", file.absolutePath,
            "-show_entries", "format=duration",
            "-v", "quiet",
            "-of", "csv=p=0"
        ).toDouble()
    }

    fun runCommand(vararg cmd: String): String {
        return ProcessBuilder(*cmd)
            .redirectErrorStream(true)
            .start()
            .inputStream
            .bufferedReader()
            .readText()
            .trim()
    }
}
