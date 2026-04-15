package ru.prohor.universe.padawan.kotlin.spring.musicmvp

import com.fasterxml.jackson.module.kotlin.readValue
import ru.prohor.universe.jocasta.core.utils.FileSystemUtils
import ru.prohor.universe.padawan.PadawanKt
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString

val music: Path = FileSystemUtils.userHome().asPath().resolve("Downloads/music")
val cover: Path = FileSystemUtils.userHome().asPath().resolve("Downloads/cover")

fun main() {

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
    fun getDuration(track: String): Double {
        return runCommand(
            "ffprobe",
            "-i", music.resolve(track).absolutePathString(),
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
