package ru.prohor.universe.uni.cli

import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import java.io.File
import kotlin.system.exitProcess

class Launcher {
    private val universeHome = System.getenv("UNIVERSE_HOME") ?: err("UNIVERSE_HOME is not set")
    private val baseDir = File(universeHome, "c3po/uni")
    private val cacheDir = File(baseDir, ".uni-cache")
    private val cpFile = File(cacheDir, "classpath")
    private val lastModifiedFile = File(cacheDir, "last-modified")

    fun run() {
        cacheDir.mkdirs()
        if (!baseDir.exists()) {
            err("Base dir not found: $baseDir")
        }

        val srcLastModified = findLastModified(File(baseDir, "src/main/kotlin"))
        val cachedLastModified = lastModifiedFile.takeIf { it.exists() }?.readText()?.trim()?.toLong()
        if (srcLastModified == cachedLastModified) return

        println(yellow("Building new version of uni cli..."))
        try {
            runCmd(baseDir, "mvn", "-q", "-DskipTests", "compile")
            runCmd(baseDir, "mvn", "-q", "dependency:build-classpath", "-Dmdep.outputFile=${cpFile.absolutePath}")
        } catch (e: Exception) {
            err("Build failed: ${e.message}")
        }
        lastModifiedFile.writeText(srcLastModified.toString())
        println(green("Build complete. Please, rerun your command"))
        exitProcess(0)
    }

    private fun err(msg: String): Nothing {
        System.err.println(red(msg))
        exitProcess(1)
    }

    private fun findLastModified(srcDir: File): Long {
        val lastModified = srcDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .map { it.lastModified() }
            .maxOf { it }

        return lastModified
    }

    private fun runCmd(dir: File, vararg cmd: String) {
        val process = ProcessBuilder(*cmd)
            .directory(dir)
            .inheritIO()
            .start()

        if (process.waitFor() != 0) {
            err("Command failed: ${cmd.joinToString(" ")}")
        }
    }
}
