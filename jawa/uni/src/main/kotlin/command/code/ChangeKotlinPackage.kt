package ru.prohor.universe.uni.cli.command.code

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import ru.prohor.universe.uni.cli.command.UniCommand
import java.io.File

class ChangeKotlinPackage : UniCommand("kt-pkg") {
    private val directory by argument(help = "directory in which kotlin files will be changed")
        .file(mustExist = true, canBeFile = false, canBeSymlink = false)
    private val oldPackage by argument(help = "old package")
    private val newPackage by argument(help = "new package")

    override fun help(context: Context) = "change packages of all kotlin files in the specified directory"

    override fun run() {
        var processedCount = 0

        (directory.listFiles() ?: emptyArray<File>())
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val text = file.readText()
                val firstLine = text.lineSequence().first()
                if (firstLine != "package $oldPackage") {
                    println("illegal package: '$firstLine' at ${file.relativeTo(directory)}")
                    return@forEach
                }
                val newLine = "package $newPackage"
                val newContent = text.replaceFirst(firstLine, newLine)
                file.writeText(newContent)
                processedCount++
            }

        println("Done! Successfully processed $processedCount files")
    }
}
