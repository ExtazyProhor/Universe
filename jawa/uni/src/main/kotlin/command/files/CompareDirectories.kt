package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.util.fileHash
import java.io.File
import java.security.MessageDigest

class CompareDirectories : UniCommand(name = "compare-directories") {
    private val ignoreNames by option("-i", "--ignore-names", help = "ignore file / directory names").flag()
    private val first by argument("directory-1").file(mustExist = true, canBeFile = false)
    private val second by argument("directory-2").file(mustExist = true, canBeFile = false)

    override fun help(context: Context) = "compares two directories"

    override fun run() {
        val hash1 = directoryHash(first, ignoreNames)
        val hash2 = directoryHash(second, ignoreNames)

        if (hash1 == hash2) {
            echo(green("directories are identical"))
        } else {
            echo(yellow("directories are different"))
        }
    }

    private fun directoryHash(directory: File, ignoreNames: Boolean): String {
        require(directory.isDirectory)
        return hashDirectory(directory, ignoreNames)
    }

    private fun hashDirectory(dir: File, ignoreNames: Boolean): String {
        val children = dir.listFiles().orEmpty()
        val signatures = children.map { child ->
            if (child.isFile) {
                val hash = fileHash(child)
                if (ignoreNames) "F:$hash" else "F:${child.name}:$hash"
            } else {
                val hash = hashDirectory(child, ignoreNames)
                if (ignoreNames) "D:$hash" else "D:${child.name}:$hash"
            }
        }.sorted()

        val md5 = MessageDigest.getInstance("MD5")
        signatures.forEach {
            md5.update(it.toByteArray())
        }
        return md5.digest().joinToString("") { "%02x".format(it) }
    }
}
