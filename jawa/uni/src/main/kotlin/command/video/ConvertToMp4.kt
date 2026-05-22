package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class ConvertToMp4 : FfmpegCommand(name = "mp4") {
    private val withRecoding by option("-w", "--with-recoding", help = "recode target video to h.264").flag()

    override val file by argument(help = "file that needs to be converted to mp4")

    override fun help(context: Context) = "converts mkv file to mp4"

    private val commandWithoutRecoding by lazy {
        listOf(
            "ffmpeg",
            "-i", file,
            "-c:v", "copy",
            "-c:a", "aac",
            "-b:a", "192k",
            "-ac", "2",
            "-movflags", "+faststart",
            "-progress", "pipe:1",
            "-nostats",
            file.removeSuffix(".mkv") + ".mp4"
        )
    }

    private val commandWithRecoding by lazy {
        listOf(
            "ffmpeg",
            "-i", file,
            "-map", "0:v:0", "-map", "0:a:0",
            "-c:v", "libx264",
            "-profile:v", "high",
            "-level", "4.1",
            "-pix_fmt", "yuv420p",
            "-c:a", "aac",
            "-b:a", "192k",
            "-ac", "2",
            "-movflags", "+faststart",
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
        runFfmpeg(if (withRecoding) commandWithRecoding else commandWithoutRecoding)
    }
}
