package ru.prohor.universe.uni.cli.helper.fslinter

import java.text.Normalizer

enum class FixMode { SMART, UNDERSCORE }

/** Turns an invalid name into a valid one. Pure string transformation, no
 *  filesystem access and no knowledge of sibling names (collision handling
 *  across siblings is [FixPlanner]'s job)
 */
object NameFixers {
    private val SMART_REPLACEMENTS: Map<Char, String> = mapOf(
        ':' to "-", '/' to "-", '\\' to "-", '|' to "-",
        '?' to "", '*' to "", '<' to "(", '>' to ")", '"' to "'",
    )

    fun fix(name: String, mode: FixMode): String = when (mode) {
        FixMode.SMART -> smartFix(name)
        FixMode.UNDERSCORE -> underscoreFix(name)
    }

    private fun smartFix(name: String): String {
        val sb = StringBuilder()
        for (ch in name) {
            if (ch in NameRules.CONTROL_CHARS) continue
            sb.append(SMART_REPLACEMENTS[ch] ?: ch.toString())
        }
        var fixed = Normalizer.normalize(sb.toString(), Normalizer.Form.NFC)
        fixed = Regex(" {2,}").replace(fixed, " ")
        fixed = fixed.trim(' ', '.')
        fixed = guardReserved(fixed)
        if (fixed.isEmpty()) fixed = "_"
        return fixed
    }

    private fun underscoreFix(name: String): String {
        val sb = StringBuilder()
        for (ch in name) {
            if (ch in NameRules.FORBIDDEN_CHARS || ch in NameRules.CONTROL_CHARS) {
                sb.append('_')
            } else {
                sb.append(ch)
            }
        }
        var fixed = Normalizer.normalize(sb.toString(), Normalizer.Form.NFC).trim(' ', '.')
        fixed = guardReserved(fixed)
        if (fixed.isEmpty()) fixed = "_"
        return fixed
    }

    private fun guardReserved(name: String): String {
        val base = name.substringBefore(".").uppercase()
        return if (base in NameRules.RESERVED_BASENAMES) "_$name" else name
    }
}
