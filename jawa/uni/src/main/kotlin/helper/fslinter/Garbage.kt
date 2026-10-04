package ru.prohor.universe.uni.cli.helper.fslinter

/**
 * Files and folders created by the OS itself (Finder, Explorer) that are not
 * part of the user's actual data
 */
object Garbage {
    private val FILE_NAMES: Set<String> = setOf(".DS_Store", "Thumbs.db", "desktop.ini", ".apdisk", "Icon\r")
    private val FILE_PREFIXES: List<String> = listOf("._")
    private val DIR_NAMES: Set<String> = setOf(
        ".Spotlight-V100", ".fseventsd", ".Trashes", ".TemporaryItems",
        $$"$RECYCLE.BIN", "System Volume Information"
    )

    fun isGarbage(name: String, isDir: Boolean): Boolean {
        return if (isDir) {
            name in DIR_NAMES
        } else {
            name in FILE_NAMES || FILE_PREFIXES.any { name.startsWith(it) }
        }
    }
}
