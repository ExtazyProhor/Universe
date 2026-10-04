package ru.prohor.universe.uni.cli.helper.fslinter

enum class DiffOp { EQUAL, REMOVED, ADDED }
data class DiffPart(val op: DiffOp, val text: String)

/** Character-level diff between an old and a new name, based on the longest
 *  common subsequence. Returns two part lists - one for rendering the old
 *  name (EQUAL/REMOVED segments) and one for the new name (EQUAL/ADDED
 *  segments). So a caller can show both lines with only the changed
 *  fragments highlighted, instead of the whole name
 */
object DiffHighlighter {
    private sealed class Op {
        data class Equal(val ch: Char) : Op()
        data class Removed(val ch: Char) : Op()
        data class Added(val ch: Char) : Op()
    }

    fun diff(old: String, new: String): Pair<List<DiffPart>, List<DiffPart>> {
        val ops = computeOps(old, new)
        return groupOldParts(ops) to groupNewParts(ops)
    }

    private fun computeOps(old: String, new: String): List<Op> {
        val n = old.length
        val m = new.length
        val dp = Array(n + 1) { IntArray(m + 1) }
        for (i in n - 1 downTo 0) {
            for (j in m - 1 downTo 0) {
                dp[i][j] = if (old[i] == new[j]) {
                    dp[i + 1][j + 1] + 1
                } else {
                    maxOf(dp[i + 1][j], dp[i][j + 1])
                }
            }
        }

        val ops = mutableListOf<Op>()
        var i = 0
        var j = 0
        while (i < n && j < m) {
            when {
                old[i] == new[j] -> {
                    ops.add(Op.Equal(old[i]))
                    i++; j++
                }

                dp[i + 1][j] >= dp[i][j + 1] -> {
                    ops.add(Op.Removed(old[i]))
                    i++
                }

                else -> {
                    ops.add(Op.Added(new[j]))
                    j++
                }
            }
        }
        while (i < n) {
            ops.add(Op.Removed(old[i])); i++
        }
        while (j < m) {
            ops.add(Op.Added(new[j])); j++
        }
        return ops
    }

    private fun groupOldParts(ops: List<Op>) = groupParts(ops, { _ -> null }, { op -> DiffOp.REMOVED to op.ch })

    private fun groupNewParts(ops: List<Op>) = groupParts(ops, { op -> DiffOp.ADDED to op.ch }, { _ -> null })

    private fun groupParts(
        ops: List<Op>,
        addedPair: (Op.Added) -> Pair<DiffOp, Char>?,
        removedPair: (Op.Removed) -> Pair<DiffOp, Char>?
    ): List<DiffPart> {
        val parts = mutableListOf<DiffPart>()
        val buf = StringBuilder()
        var current: DiffOp? = null

        fun flush() {
            if (buf.isNotEmpty() && current != null) parts.add(DiffPart(current!!, buf.toString()))
            buf.clear()
        }

        for (op in ops) {
            val kindCh: Pair<DiffOp, Char>? = when (op) {
                is Op.Equal -> DiffOp.EQUAL to op.ch
                is Op.Removed -> removedPair.invoke(op)
                is Op.Added -> addedPair.invoke(op)
            }
            if (kindCh == null) continue
            val (kind, ch) = kindCh
            if (kind != current) {
                flush()
                current = kind
            }
            buf.append(ch)
        }
        flush()
        return parts
    }
}
