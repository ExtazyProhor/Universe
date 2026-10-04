package ru.prohor.universe.uni.cli.helper.fslinter

import java.text.Normalizer

/**
 * Validation rules for a single path component (a file or folder name),
 * chosen so that a name passing all checks is valid on both macOS/Linux and
 * Windows file systems.
 */
object NameRules {
    val FORBIDDEN_CHARS = "<>:\"/\\|?*".toSet()
    val CONTROL_CHARS = (0..31).map { it.toChar() }.toSet()

    val RESERVED_BASENAMES = buildSet {
        addAll(listOf("CON", "PRN", "AUX", "NUL"))
        for (i in 1..9) add("COM$i")
        for (i in 1..9) add("LPT$i")
    }

    const val MAX_COMPONENT_BYTES = 255
    const val WARN_TOTAL_PATH_CHARS = 260

    /** Pure check of one name. Does not touch the filesystem. */
    fun checkName(name: String, allowNfd: Boolean = false): List<Issue> {
        val issues = mutableListOf<Issue>()

        if (name.isEmpty() || name == "." || name == "..") {
            issues.add(Issue(IssueCode.EMPTY_OR_DOT))
            return issues
        }

        val badChars = name.toSet().intersect(FORBIDDEN_CHARS).sorted()
        if (badChars.isNotEmpty()) {
            issues.add(Issue(IssueCode.FORBIDDEN_CHARS, badChars.joinToString(" ")))
        }

        val ctrl = name.toSet().intersect(CONTROL_CHARS).sorted()
        if (ctrl.isNotEmpty()) {
            val codes = ctrl.joinToString(", ") { "0x%02x".format(it.code) }
            issues.add(Issue(IssueCode.CONTROL_CHARS, codes))
        }

        if (name.last() == ' ' || name.last() == '.') {
            issues.add(Issue(IssueCode.TRAILING_SPACE_OR_DOT))
        }
        if (name.first() == ' ') {
            issues.add(Issue(IssueCode.LEADING_SPACE))
        }

        val base = name.substringBefore(".").trimEnd('.', ' ').uppercase()
        if (base in RESERVED_BASENAMES) {
            issues.add(Issue(IssueCode.RESERVED_NAME, base))
        }

        if (!allowNfd && !Normalizer.isNormalized(name, Normalizer.Form.NFC)) {
            issues.add(Issue(IssueCode.NOT_NFC))
        }

        val byteLen = name.toByteArray(Charsets.UTF_8).size
        if (byteLen > MAX_COMPONENT_BYTES) {
            issues.add(Issue(IssueCode.COMPONENT_TOO_LONG, "$byteLen UTF-8 bytes"))
        }
        return issues
    }

    /** Key used for case-insensitive, Unicode-normalized name comparison. */
    fun comparisonKey(name: String) = Normalizer.normalize(name, Normalizer.Form.NFC).lowercase()
}
