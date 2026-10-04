package ru.prohor.universe.uni.cli.helper.fslinter

object LongPaths {
    /** Flags entries whose path, once copied under a destination folder of
     *  [prefixLength] characters, would exceed the classic Windows MAX_PATH
     *  limit of [limit] characters. */
    fun find(
        entries: List<FileSystemEntry>,
        prefixLength: Int,
        limit: Int = NameRules.WARN_TOTAL_PATH_CHARS,
    ): List<Pair<FileSystemEntry, Int>> {
        return entries.mapNotNull { e ->
            val total = e.rel.toString().length + prefixLength
            if (total > limit) e to total else null
        }
    }
}
