package ru.prohor.universe.uni.cli.helper.fslinter

import java.nio.file.Path

/**
 * One file or folder found while walking a tree.
 *
 * @param path absolute path on disk
 * @param rel path relative to the scan root
 * @param isDir whether this entry is a directory
 * @param depth 0 for entries directly inside the scan root
 */
data class FileSystemEntry(
    val path: Path,
    val rel: Path,
    val isDir: Boolean,
    val depth: Int,
)
