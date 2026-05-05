package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

class CutFragment : FfmpegCommand(name = "cut") {
    override val file by argument(help = "input video file")
    private val output by argument(help = "output video file")

    private val from by option("--from", help = "start time (HH:MM:SS)")
    private val to by option("--to", help = "end time (HH:MM:SS)")
    private val resolution by option("--resolution", help = "vertical resolution (e.g. 1080)").default("720")
    private val fps by option("--fps", help = "Frames per second").default("30")

    override fun run() {
        val command = mutableListOf(
            "ffmpeg",
            "-i", file
        )
        if (from != null) {
            command += listOf("-ss", from!!)
        }
        if (to != null) {
            command += listOf("-to", to!!)
        }
        command += listOf(
            "-vf", "scale=-1:$resolution,fps=$fps",
            "-c:v", "libx264",
            "-crf", "23",
            "-c:a", "copy",
            output
        )
        runFfmpeg(command)
    }
}
