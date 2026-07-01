package ru.prohor.universe.uni.cli.command.files

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import ru.prohor.universe.uni.cli.command.UniCommand
import java.io.File
import java.nio.file.Paths
import kotlin.math.ln
import kotlin.math.pow

class TreeSize : UniCommand("tree-size") {
    val path: String by argument(help = "path to target directory").default(".")
    val maxDepth: Int? by option("-d", "--depth", "-L", help = "maximum nesting depth").int()
    val excludeHidden: Boolean by option("-h", "--exclude-hidden", help = "exclude hidden files and directories").flag()

    override fun help(context: Context) = "prints tree of files and directories with their sizes"

    override fun run() {
        val rootFile = File(Paths.get(path).toAbsolutePath().toString())
        if (!rootFile.exists() || !rootFile.isDirectory) {
            echo("Specified path does not exist or is not a directory", err = true)
            return
        }

        val rootNode = buildTree(rootFile)
        printTree(rootNode, "", true, currentDepth = 0)
    }

    class Node(val file: File) {
        val isDirectory = file.isDirectory
        val isHidden = file.isHidden
        val children = mutableListOf<Node>()
        var size: Long = 0
    }

    private fun buildTree(file: File): Node {
        val node = Node(file)
        if (file.isDirectory) {
            val listFiles = file.listFiles() ?: emptyArray()
            for (childFile in listFiles) {
                if (excludeHidden && childFile.isHidden) {
                    continue
                }
                val childNode = buildTree(childFile)
                node.children.add(childNode)
                node.size += childNode.size
            }
        } else {
            node.size = file.length()
        }
        node.children.sortWith(compareByDescending<Node> { it.isDirectory }.thenByDescending { it.size })
        return node
    }

    private fun printTree(node: Node, prefix: String, isLast: Boolean, currentDepth: Int) {
        val marker = if (currentDepth == 0) "" else if (isLast) "└── " else "├── "
        val formattedSize = formatSize(node.size)
        val name = if (node.isDirectory) "${node.file.name}/" else node.file.name

        val coloredName = when {
            node.isHidden -> red(name)
            node.isDirectory -> cyan(name)
            else -> green(name)
        }

        echo("$prefix$marker$coloredName ($formattedSize)")

        val maxDepth = maxDepth
        if (maxDepth != null && currentDepth >= maxDepth) return

        val nextPrefix = prefix + if (currentDepth == 0) "" else if (isLast) "    " else "│   "
        val childrenCount = node.children.size

        node.children.forEachIndexed { index, child ->
            val childIsLast = index == childrenCount - 1
            printTree(child, nextPrefix, childIsLast, currentDepth + 1)
        }
    }

    private fun formatSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val exp = (ln(bytes.toDouble()) / ln(1024.0)).toInt()
        val units = listOf("KB", "MB", "GB", "TB", "PB")
        val unit = units[exp - 1]
        return String.format("%.2f %s", bytes / 1024.0.pow(exp.toDouble()), unit)
    }
}
