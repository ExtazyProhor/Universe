package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import ru.prohor.universe.uni.cli.util.runCommand

class ConvertToMp4 : FfmpegCommand() {
    override val file by argument(help = "file that needs to be converted to mp4")

    override fun help(context: Context) = "converts mkv file to mp4 without recoding"

    override val fullCommand by lazy {
        listOf(
            "ffmpeg",
            "-i", file,
            "-c:v", "copy",
            "-c:a", "aac",
            "-b:a", "192k",
            "-progress", "pipe:1",
            "-nostats",
            file.removeSuffix(".mkv") + ".mp4"
        )
    }

    override fun run() {
        if (!file.endsWith(".mkv")) {
            errorEcho("file must ends with .mkv")
            return
        }
        runCommand()
    }

    private fun getDuration(file: String): Long? {
        val result = runCommand(
            listOf(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                file
            )
        )
        return result.stdout.trim().toDoubleOrNull()?.let { it * 1_000_000 }?.toLong()
    }
}
