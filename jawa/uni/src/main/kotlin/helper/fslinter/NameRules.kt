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
    fun checkName(name: String): List<Issue> {
        val issues = mutableListOf<Issue>()

        if (name.isEmpty() || name == "." || name == "..") {
            issues.add(Issue(IssueCode.EMPTY_OR_DOT, "Empty name or '.'/'..'"))
            return issues
        }

        val badChars = name.toSet().intersect(FORBIDDEN_CHARS).sorted()
        if (badChars.isNotEmpty()) {
            issues.add(Issue(IssueCode.FORBIDDEN_CHARS, "Forbidden characters: ${badChars.joinToString(" ")}"))
        }

        val ctrl = name.toSet().intersect(CONTROL_CHARS).sorted()
        if (ctrl.isNotEmpty()) {
            val codes = ctrl.joinToString(", ") { "0x%02x".format(it.code) }
            issues.add(Issue(IssueCode.CONTROL_CHARS, "Control characters (codes): $codes"))
        }

        if (name.last() == ' ' || name.last() == '.') {
            issues.add(Issue(IssueCode.TRAILING_SPACE_OR_DOT, "Name ends with a space or a dot (invalid on Windows)"))
        }
        if (name.first() == ' ') {
            issues.add(Issue(IssueCode.LEADING_SPACE, "Name starts with a space"))
        }

        val base = name.substringBefore(".").trimEnd('.', ' ').uppercase()
        if (base in RESERVED_BASENAMES) {
            issues.add(Issue(IssueCode.RESERVED_NAME, "Reserved Windows device name: $base"))
        }

        val nfc = Normalizer.normalize(name, Normalizer.Form.NFC)
        if (nfc != name) {
            issues.add(
                Issue(
                    IssueCode.NOT_NFC,
                    "Name is not NFC-normalized (common on macOS, causes duplicates on Windows/in git)"
                )
            )
        }

        val byteLen = name.toByteArray(Charsets.UTF_8).size
        if (byteLen > MAX_COMPONENT_BYTES) {
            issues.add(
                Issue(
                    IssueCode.COMPONENT_TOO_LONG,
                    "Component is $byteLen UTF-8 bytes (limit $MAX_COMPONENT_BYTES)"
                )
            )
        }
        return issues
    }

    /** Key used for case-insensitive, Unicode-normalized name comparison. */
    fun comparisonKey(name: String) = Normalizer.normalize(name, Normalizer.Form.NFC).lowercase()
}
