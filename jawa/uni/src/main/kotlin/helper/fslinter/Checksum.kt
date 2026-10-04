package ru.prohor.universe.uni.cli.helper.fslinter

import org.apache.commons.codec.digest.DigestUtils
import java.nio.file.Files
import java.nio.file.Path
import java.text.Normalizer

data class ChecksumNode(
    val name: String,
    val hash: String,
    val isDir: Boolean,
    val rel: String,
    val children: List<ChecksumNode>?,
)

data class ChecksumResult(val tree: List<ChecksumNode>, val rootHash: String)

/** Recursive content checksum of a directory tree.
 *
 *  A file's hash is the SHA-256 of its bytes. A directory's hash is the
 *  SHA-256 of the sorted, NFC-normalized list of "isDir:name:hash" lines of
 *  its direct children. That makes the root hash independent of traversal
 *  order and of OS-specific filename encoding (NFC vs NFD), so the same
 *  logical tree produces the same hash on macOS and on Windows
 */
object Checksum {
    fun hashFile(path: Path): String {
        return Files.newInputStream(path).use { input -> DigestUtils.sha256Hex(input) }
    }

    fun build(root: Path): ChecksumResult {
        val absRoot = root.toAbsolutePath().normalize()
        val (tree, hash) = buildDir(absRoot, absRoot)
        return ChecksumResult(tree, hash)
    }

    private fun buildDir(dir: Path, root: Path): Pair<List<ChecksumNode>, String> {
        val children = try {
            Files.newDirectoryStream(dir).use { stream ->
                stream.sortedBy { it.fileName.toString().lowercase() }
            }
        } catch (_: Exception) {
            emptyList()
        }

        val nodes = mutableListOf<ChecksumNode>()
        for (child in children) {
            if (Files.isSymbolicLink(child)) continue
            val isDir = Files.isDirectory(child)
            val name = child.fileName.toString()
            if (Garbage.isGarbage(name, isDir)) continue
            val nameNfc = Normalizer.normalize(name, Normalizer.Form.NFC)
            val rel = root.relativize(child).toString().replace('\\', '/')
            if (isDir) {
                val (subNodes, subHash) = buildDir(child, root)
                nodes.add(ChecksumNode(nameNfc, subHash, true, rel, subNodes))
            } else {
                val h = hashFile(child)
                nodes.add(ChecksumNode(nameNfc, h, false, rel, null))
            }
        }

        val listing = nodes.joinToString("\n") { "${it.isDir}:${it.name}:${it.hash}" }
        val dirHash = DigestUtils.sha256Hex(listing)
        return nodes to dirHash
    }
}
