package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import ru.prohor.universe.uni.cli.command.UniCommand
import ru.prohor.universe.uni.cli.helper.fslinter.Checksum
import ru.prohor.universe.uni.cli.helper.fslinter.ChecksumNode
import kotlin.io.path.Path

class ChecksumCommand : UniCommand(name = "checksum") {
    override fun help(context: Context) = "compute a recursive content checksum tree or find duplicate files"

    private val directory by argument(help = "directory to hash")
        .path(mustExist = true, canBeFile = false, canBeDir = true, mustBeReadable = true)
        .default(Path("."))
    private val maxDepth by option("-L", "--depth", help = "maximum nesting depth to print").int()
        .default(Int.MAX_VALUE)
    private val allowNfd by option(
        "--nfd", "--allow-nfd",
        help = "hash and display original NFD names without NFC normalization"
    ).flag()
    private val duplicates by option(
        "-d", "--duplicates", help = "print groups of files with identical contents and paths relative to directory"
    ).flag()

    override fun run() {
        val result = Checksum.build(directory, normalizeNames = !allowNfd)
        if (duplicates) {
            printDuplicates(result.tree)
            return
        }
        val root = ChecksumNode(
            name = "${directory.toAbsolutePath().normalize()}/",
            hash = result.rootHash,
            isDir = true,
            rel = "",
            children = result.tree,
        )
        printTree(root, "", true, 0)
    }

    private fun printDuplicates(tree: List<ChecksumNode>) {
        fun files(nodes: List<ChecksumNode>): List<ChecksumNode> = nodes.flatMap { node ->
            if (node.isDir) files(node.children.orEmpty()) else listOf(node)
        }

        val groups = files(tree).groupBy { it.hash }
            .filterValues { it.size > 1 }
            .toSortedMap()
        if (groups.isEmpty()) {
            echo("No duplicates found")
            return
        }
        groups.entries.forEachIndexed { index, (hash, nodes) ->
            if (index > 0) echo("")
            echo(yellow(hash))
            nodes.map { it.rel }.sorted().forEach { echo("  ${green(it)}") }
        }
    }

    private fun printTree(node: ChecksumNode, prefix: String, isLast: Boolean, depth: Int) {
        val marker = if (depth == 0) "" else if (isLast) "└── " else "├── "
        val name = if (node.isDir && depth > 0) "${node.name}/" else node.name
        val coloredName = when {
            node.name.startsWith(".") -> red(name)
            node.isDir -> cyan(name)
            else -> green(name)
        }
        val hash = if (depth == 0) node.hash else node.hash.take(12)
        echo("$prefix$marker$coloredName ($hash)")

        if (depth < maxDepth) {
            val nextPrefix = prefix + if (depth == 0) "" else if (isLast) "    " else "│   "
            val children = node.children.orEmpty().sortedWith(
                compareByDescending<ChecksumNode> { it.isDir }.thenBy { it.name.lowercase() }.thenBy { it.name }
            )
            children.forEachIndexed { index, child ->
                printTree(child, nextPrefix, index == children.lastIndex, depth + 1)
            }
        }
    }
}
