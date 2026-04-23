package ru.prohor.universe.uni.cli.command.video

import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.animation.progress.animateOnThread
import com.github.ajalt.mordant.animation.progress.execute
import com.github.ajalt.mordant.animation.progress.update
import com.github.ajalt.mordant.widgets.progress.percentage
import com.github.ajalt.mordant.widgets.progress.progressBar
import com.github.ajalt.mordant.widgets.progress.progressBarLayout
import com.github.ajalt.mordant.widgets.progress.text
import com.github.ajalt.mordant.widgets.progress.timeRemaining
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.colored
import ru.prohor.universe.uni.cli.util.runCommand
import ru.prohor.universe.uni.cli.util.runCommandStreaming

abstract class FfmpegCommand : UniCommand() {
    abstract val file: String

    abstract val fullCommand: List<String>

    protected fun runCommand() {
        if (!requireCommand("ffmpeg")) return
        if (!requireCommand("ffprobe")) return

        getDuration(file)?.let { duration ->
            val progress = progressBarLayout {
                text("Processing... ")
                percentage()
                progressBar()
                timeRemaining()
            }.animateOnThread(terminal)

            val future = progress.execute()
            progress.update { total = duration }

            try {
                runCommandStreaming(fullCommand) { line ->
                    if (line.startsWith("out_time_ms=")) {
                        val time = line.removePrefix("out_time_ms=").toLongOrNull() ?: 0L
                        progress.update(time)
                    }
                }
            } finally {
                progress.clear()
                progress.stop()
                future.get()
            }

        } ?: run {
            echo("Processing...")
            runCommand(fullCommand)
        }
        colored {
            echo("$file " + "successfully processed".green)
        }
    }

    private fun getDuration(videoFile: String): Long? {
        val result = runCommand(
            listOf(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoFile
            )
        )
        return result.stdout.trim().toDoubleOrNull()?.let { it * 1_000_000 }?.toLong()
    }
}
