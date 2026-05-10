package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.rendering.TextColors.magenta
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.video.StreamsParser
import ru.prohor.universe.uni.cli.util.runCommand
import java.io.File

class Streams : UniCommand() {
    private val validExtensions = setOf("mkv", "mp4", "avi", "mov", "webm")
    private val file by argument(help = "file whose streams will be shown").file(mustExist = true)

    override fun run() {
        if (!isValidExtension(file)) {
            errorEcho("Illegal extension: ${file.extension}, must be one of $validExtensions")
            return
        }
        printForFile(file)
    }

    private fun printForFile(file: File) {
        val json = getFFProbeJson(file) ?: return
        val streamsInfo = StreamsParser().parse(json)

        printStreams(streamsInfo.videos, "video")
        printStreams(streamsInfo.audios, "audio")
        printStreams(streamsInfo.subtitles, "subtitle")
        printStreams(streamsInfo.unknownStreams, "unknown streams")
    }

    private fun printStreams(streams: List<*>, header: String) {
        if (streams.isEmpty()) return

        printHeader(header)
        streams.forEach { echo(it) }
    }

    private fun printHeader(header: String) {
        val message = " $header "
        val width = terminal.size.width
        val dashCount = (width - message.length) / 2

        val finalLine = if (dashCount > 0) {
            val dashes = "-".repeat(dashCount)
            val line = dashes + message + dashes
            if (line.length < width) "$line-" else line
        } else {
            message
        }
        echo(magenta(finalLine))
    }

    private fun isValidExtension(file: File) = validExtensions.contains(file.extension)

    private fun getFFProbeJson(file: File): String? {
        val result = runCommand(
            "ffprobe",
            "-v", "error",
            "-print_format", "json",
            "-show_streams",
            file.absolutePath
        )
        if (result.exitCode != 0) {
            errorEcho(result.stderr)
            return null
        }
        return result.stdout
    }
}
