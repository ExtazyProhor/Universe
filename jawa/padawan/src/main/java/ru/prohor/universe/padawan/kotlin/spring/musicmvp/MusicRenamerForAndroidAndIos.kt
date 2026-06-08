package ru.prohor.universe.padawan.kotlin.spring.musicmvp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mpatric.mp3agic.ID3v24Tag
import com.mpatric.mp3agic.Mp3File
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils
import java.io.File

fun main() {

}

fun processForIos() {
    val android = FileSystemUtils.userHome().asFile().resolve("Downloads/dacha-bikes-android")
    val tracks = jacksonObjectMapper()
        .readValue<List<Track>>(File("~/Downloads/music-player/all_lyrics.json"))
        .toMutableList()
    val map = tracks.associateBy { it.track }

    (android.listFiles() ?: emptyArray<File>())
        .filter { it.name.endsWith(".mp3") }
        .map { (Ffmpeg.extractMetadata(it).format.tags?.artist ?: "") to it }
        .forEach { aaa ->
            val track = map[aaa.second.name] ?: throw RuntimeException()
            val full = aaa.first + ". " + track.name + " - " + track.authors.joinToString() + ".mp3"
            aaa.second.renameTo(android.resolve(full))
        }
}

fun processForAndroid(
    i: Int,
    track: Track,
    sourceDir: File,
    targetDir: File
) {
    val sourceFile = File(sourceDir, track.track)
    val title = "${track.name} - ${track.authors.joinToString(", ")}"
    val artist = i.let { "$i" }.let { "0".repeat(4 - it.length) + it }

    val mp3 = Mp3File(sourceFile)

    val tag = ID3v24Tag()
    tag.title = title
    tag.artist = artist
    mp3.id3v2Tag = tag

    val targetFile = File(targetDir, track.track)

    mp3.save(targetFile.absolutePath)
}
