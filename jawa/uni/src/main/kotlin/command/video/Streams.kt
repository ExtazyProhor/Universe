package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextStyles.bold
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.video.StreamsParser
import ru.prohor.universe.uni.cli.util.runCommand
import java.io.File

class Streams : UniCommand() {
    private val validExtensions = setOf("mkv", "mp4", "avi", "mov", "webm")
    private val file by argument(help = "file whose streams will be shown").file(mustExist = true)
    private val directory by option(
        "-d", "--directory", help = "shows streams of all valid files in a directory, the <file> argument expects a directory path"
    ).flag()
    private val recursive by option("-r", "--recursive", help = "iterate files recursively").flag()

    override fun help(context: Context) = "prints video file streams using ffprobe"

    override fun run() {
        if (recursive) {
            printForFiles(file.walkTopDown())
            return
        }
        if (directory) {
            printForFiles(file.listFiles()?.asSequence() ?: emptySequence())
            return
        }
        if (!isValidExtension(file)) {
            errorEcho("Illegal extension: ${file.extension}, must be one of $validExtensions")
            return
        }
        printForFile(file)
    }

    private fun printForFiles(files: Sequence<File>) {
        files.filter { isValidExtension(it) }.forEach { file ->
            echo((cyan + bold)(createHeader(file.relativeTo(this.file).path)))
            printForFile(file)
        }
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

        echo(magenta(createHeader(header)))
        streams.forEach { echo(it) }
    }

    private fun createHeader(header: String): String {
        val message = " $header "
        val width = terminal.size.width
        val dashCount = (width - message.length) / 2

        return if (dashCount > 0) {
            val dashes = "-".repeat(dashCount)
            if (dashCount * 2 + message.length < width) "$dashes-$message$dashes" else "$dashes$message$dashes"
        } else {
            message
        }
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
