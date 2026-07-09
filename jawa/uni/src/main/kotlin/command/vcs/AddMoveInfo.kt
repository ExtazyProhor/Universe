package ru.prohor.universe.uni.cli.command.vcs

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import ru.prohor.universe.uni.cli.command.UniCommand

class AddMoveInfo : UniCommand(name = "add-move-info") {
    private val file by argument(
        help = "file from which the status output will be taken. If not specified, status output are taken from stdin"
    ).file(mustExist = true).optional()

    override fun help(context: Context) =
        "adds information to arc that the file has been moved and not deleted and recreated"

    override fun run() {
        val reader = file?.bufferedReader() ?: System.`in`.bufferedReader()
        val changedFiles = reader.useLines { findChangedFiles(it) }

        changedFiles.forEach { file ->
            errorOutputRunCommand("mv", file.newFilePath, file.deletedPath)
        }
        errorOutputRunCommand("arc", "add", ".")
        changedFiles.forEach { file ->
            errorOutputRunCommand("arc", "mv", file.deletedPath, file.newFilePath)
        }
    }

    private fun findChangedFiles(sequence: Sequence<String>): List<ChangedFile> {
        return sequence
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map(::parseStatusLine)
            .groupBy { it.className }
            .toList()
            .map { (_, statusLines) ->
                if (statusLines.size != 2) {
                    throw IllegalArgumentException("Illegal count of entries with same className: $statusLines")
                }
                if (statusLines[0].status == statusLines[1].status) {
                    throw IllegalArgumentException("Status of 2 entries with same className can not be same")
                }
                val file = ChangedFile(
                    deletedPath = statusLines.find { it.status == Status.DELETED }?.path ?: "",
                    newFilePath = statusLines.find { it.status == Status.NEW_FILE }?.path ?: ""
                )
                if (file.deletedPath.isBlank() || file.newFilePath.isBlank()) {
                    throw IllegalArgumentException("Illegal statusLines: $statusLines")
                }
                file
            }
    }

    private fun parseStatusLine(line: String): StatusLine {
        val deleted = line.startsWith("deleted:")
        val newFile = !deleted && line.startsWith("new file:")
        if (!deleted && !newFile) {
            throw IllegalArgumentException("illegal status: ${line.substringBefore(':')}")
        }

        val path = line.removePrefix("deleted:").removePrefix("new file:").trim()
        val className = path.substringAfterLast('/').substringBeforeLast('.')
        return StatusLine(
            status = if (deleted) Status.DELETED else Status.NEW_FILE,
            className = className,
            path = path
        )
    }

    data class StatusLine(
        val status: Status,
        val className: String,
        val path: String,
    )

    enum class Status {
        DELETED,
        NEW_FILE
    }

    data class ChangedFile(
        val deletedPath: String,
        val newFilePath: String
    )
}
