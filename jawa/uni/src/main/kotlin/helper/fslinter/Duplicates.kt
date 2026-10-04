package ru.prohor.universe.uni.cli.helper.fslinter

/** Finds groups of entries that collide under case-insensitive, NFC-normalized
 *  comparison within the same parent directory - the kind of collision that
 *  silently merges or shadows files when moving between a case-sensitive and
 *  a case-insensitive filesystem
 */
object Duplicates {
    fun findGroups(entries: List<FileSystemEntry>) = entries
        .groupBy { it.path.parent to NameRules.comparisonKey(it.path.fileName.toString()) }
        .values
        .filter { it.size > 1 }
        .toList()
}
