package ru.prohor.universe.uni.cli

import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import java.io.File
import kotlin.system.exitProcess

class Launcher {
    private val universeHome = System.getenv("UNIVERSE_HOME") ?: err("UNIVERSE_HOME is not set")
    private val jawaHome = File(universeHome, "jawa")
    private val uniHome = File(jawaHome, "uni")

    private val cacheDir = File(uniHome, ".uni-cache")
    private val lastModifiedFile = File(cacheDir, "last-modified")

    fun run() {
        if (!uniHome.exists()) {
            err("Uni home not found: $uniHome")
        }
        cacheDir.mkdirs()

        val srcLastModified = findLastModified(File(uniHome, "src/main/kotlin"))
        val cachedLastModified = lastModifiedFile.takeIf { it.exists() }?.readText()?.trim()?.toLong()
        if (srcLastModified == cachedLastModified) return

        println(yellow("Building new version of uni cli..."))

        runBuild(jawaHome)
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

    private fun runBuild(dir: File) {
        val process = ProcessBuilder("mvn", "-q", "-pl", "uni", "-am", "-DskipTests", "package")
            .directory(dir)
            .inheritIO()
            .start()

        if (process.waitFor() != 0) {
            err("Command failed: ${red(process.errorStream.bufferedReader().readText())}")
        }
    }
}
