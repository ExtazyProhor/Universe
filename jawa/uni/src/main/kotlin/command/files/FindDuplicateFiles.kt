package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.magenta
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.fileHash
import java.io.File
import kotlin.io.path.relativeTo

class FindDuplicateFiles : UniCommand(name = "duplicate-files") {
    private val ignoreNames by option("-i", "--ignore-names", help = "ignore file / directory names").flag()
    private val directory by argument(help = "target directory")
        .file(mustExist = true, canBeFile = false, canBeDir = true)
        .default(File("."))

    override fun help(context: Context) = "finds duplicate files in the directory"

    override fun run() {
        val baseDir = directory.toPath().toAbsolutePath().normalize()
        val filesBySize = directory.walkTopDown()
            .filter { it.isFile }
            .filter { !EXCLUDE_FILES.contains(it.name) }
            .groupBy { it.length() }
            .filter { it.value.size > 1 }
            .values

        var duplicateCount = 0
        for (files in filesBySize) {
            val filesByHash = files.groupBy { file ->
                val md5 = fileHash(file)
                if (ignoreNames) md5 else "$md5|${file.name}"
            }.values

            for (groupFiles in filesByHash) {
                if (groupFiles.size > 1) {
                    duplicateCount++
                    echo(magenta("found group of duplicates:"))
                    groupFiles.forEach { file ->
                        val relativePath = file.toPath().toAbsolutePath().normalize().relativeTo(baseDir)
                        echo("- " + green(relativePath.toString()))
                    }
                }
            }
        }

        if (duplicateCount == 0) {
            echo("no duplicates found")
        } else {
            echo("total groups found with duplicates: $duplicateCount")
        }
    }

    companion object {
        private val EXCLUDE_FILES = setOf(".DS_Store")
    }
}
