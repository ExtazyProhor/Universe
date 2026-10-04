package ru.prohor.universe.uni.cli.helper.fslinter

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Walker {
    private fun sortedChildren(dir: Path): List<Path> = Files.newDirectoryStream(dir).use { stream ->
        stream.sortedBy { it.fileName.toString().lowercase() }
    }

    /**
     * Depth-first walk of [root]. Symlinks are never followed. Garbage
     * entries (see [Garbage]) are skipped entirely, including not descending
     * into garbage directories
     */
    fun walk(root: Path, onError: (Path, IOException) -> Unit = { _, _ -> }): List<FileSystemEntry> {
        val absRoot = root.toAbsolutePath().normalize()
        val result = mutableListOf<FileSystemEntry>()

        fun visit(dir: Path, depth: Int) {
            val children = try {
                sortedChildren(dir)
            } catch (e: IOException) {
                onError(dir, e)
                return
            }
            for (child in children) {
                if (Files.isSymbolicLink(child)) continue
                val isDir = Files.isDirectory(child)
                val name = child.fileName.toString()
                if (Garbage.isGarbage(name, isDir)) continue
                val rel = absRoot.relativize(child)
                result.add(FileSystemEntry(child, rel, isDir, depth))
                if (isDir) visit(child, depth + 1)
            }
        }

        visit(absRoot, 0)
        return result
    }

    /**
     * Finds garbage entries under [root]. A garbage directory is reported as
     * a single entry and not descended into (it is meant to be removed as a
     * whole)
     */
    fun findGarbage(root: Path, onError: (Path, IOException) -> Unit = { _, _ -> }): List<FileSystemEntry> {
        val absRoot = root.toAbsolutePath().normalize()
        val result = mutableListOf<FileSystemEntry>()

        fun visit(dir: Path, depth: Int) {
            val children = try {
                sortedChildren(dir)
            } catch (e: IOException) {
                onError(dir, e)
                return
            }
            for (child in children) {
                if (Files.isSymbolicLink(child)) continue
                val isDir = Files.isDirectory(child)
                val name = child.fileName.toString()
                val rel = absRoot.relativize(child)
                if (Garbage.isGarbage(name, isDir)) {
                    result.add(FileSystemEntry(child, rel, isDir, depth))
                } else if (isDir) {
                    visit(child, depth + 1)
                }
            }
        }

        visit(absRoot, 0)
        return result
    }
}
