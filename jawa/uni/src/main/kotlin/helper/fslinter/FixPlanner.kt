package ru.prohor.universe.uni.cli.helper.fslinter

import java.nio.file.Path

data class FixPlanItem(val entry: FileSystemEntry, val newName: String)

/** Builds a rename plan for a whole tree: only entries that currently fail
 *  [NameRules.checkName] are included, and any resulting collision with an
 *  existing or another newly-renamed sibling is resolved with a numeric
 *  suffix, " (2)", " (3)", and so on. */
object FixPlanner {
    fun plan(entries: List<FileSystemEntry>, mode: FixMode): List<FixPlanItem> {
        val rawPlan = entries.mapNotNull { e ->
            val name = e.path.fileName.toString()
            val issues = NameRules.checkName(name)
            if (issues.isEmpty()) return@mapNotNull null
            val newName = NameFixers.fix(name, mode)
            if (newName == name) null else FixPlanItem(e, newName)
        }

        // names already taken in each parent directory, by comparison key
        val taken = mutableMapOf<Path, MutableSet<String>>()
        for (e in entries) {
            taken.getOrPut(e.path.parent) { mutableSetOf() }.add(NameRules.comparisonKey(e.path.fileName.toString()))
        }

        val finalPlan = mutableListOf<FixPlanItem>()
        for (item in rawPlan) {
            val parent = item.entry.path.parent
            val used = taken.getOrPut(parent) { mutableSetOf() }
            used.remove(NameRules.comparisonKey(item.entry.path.fileName.toString()))

            var candidate = item.newName
            val dotIndex = item.newName.indexOf('.')
            val stem = if (dotIndex >= 0) item.newName.substring(0, dotIndex) else item.newName
            val ext = if (dotIndex >= 0) item.newName.substring(dotIndex) else ""
            var n = 1
            while (NameRules.comparisonKey(candidate) in used) {
                n += 1
                candidate = "$stem ($n)$ext"
            }
            used.add(NameRules.comparisonKey(candidate))
            finalPlan.add(FixPlanItem(item.entry, candidate))
        }
        return finalPlan
    }
}
