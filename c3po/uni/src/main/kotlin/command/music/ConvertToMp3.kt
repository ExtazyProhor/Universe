package ru.prohor.universe.uni.cli.command.music

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import ru.prohor.universe.uni.cli.command.video.FfmpegCommand
import ru.prohor.universe.uni.cli.util.runCommand
import java.io.File

class ConvertToMp3 : FfmpegCommand(name = "mp3") {
    override val file by argument(help = "input video file")
    private val output by option("-o", "--output", help = "output mp3 file").path()
    private val outputDirectory by option("-d", "--out-dir", help = "output directory").path()

    override fun help(context: Context) = "converts video file to mp3"

    override fun run() {
        val inputFile = File(file)
        val outputFile = output?.toFile() ?: File(inputFile.parent, "${inputFile.nameWithoutExtension}.mp3")
        val fullOutputFile = outputDirectory?.toFile()?.resolve(outputFile) ?: outputFile
        val sampleRate = getSampleRate(inputFile)

        val ffmpegCmd = mutableListOf(
            "ffmpeg", "-i", inputFile.absolutePath,
            "-map", "0:a:0",
            "-vn",
            "-c:a", "libmp3lame",
            "-b:a", "320k",
            "-ac", "2",
            "-ar", sampleRate,
            "-map_metadata", "0",
            "-id3v2_version", "3",
            "-y",
            "-progress", "pipe:1", "-nostats",
            fullOutputFile.absolutePath
        )

        runFfmpeg(ffmpegCmd)
    }

    private fun getSampleRate(file: File): String {
        val cmd = listOf(
            "ffprobe", "-v", "error", "-select_streams", "a:0",
            "-show_entries", "stream=sample_rate",
            "-of", "default=noprint_wrappers=1:nokey=1",
            file.absolutePath
        )
        val rate = runCommand(cmd).stdout.trim()
        return rate.ifEmpty { "44100" }
    }
}
