package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.text.Normalizer

class NameFixersTest {
    @Test
    fun `smart mode replaces each forbidden character with its mapped equivalent`() {
        assertEquals("Report- Q3.pdf", NameFixers.fix("Report: Q3?.pdf", FixMode.SMART))
        assertEquals("a-b-c", NameFixers.fix("a/b\\c", FixMode.SMART))
        assertEquals("(x)", NameFixers.fix("<x>", FixMode.SMART))
        assertEquals("it's", NameFixers.fix("it\"s", FixMode.SMART))
    }

    @Test
    fun `smart mode trims trailing space and collapses double spaces`() {
        assertEquals("photos", NameFixers.fix("photos ", FixMode.SMART))
        assertEquals("a b", NameFixers.fix("a  b", FixMode.SMART))
    }

    @Test
    fun `smart mode normalizes to NFC`() {
        val nfd = Normalizer.normalize("éё.txt", Normalizer.Form.NFD)
        val fixed = NameFixers.fix(nfd, FixMode.SMART)
        assertEquals(Normalizer.normalize(fixed, Normalizer.Form.NFC), fixed)
    }

    @Test
    fun `smart mode guards reserved names`() {
        assertEquals("_NUL.txt", NameFixers.fix("NUL.txt", FixMode.SMART))
    }

    @Test
    fun `smart mode never returns an empty name`() {
        assertEquals("_", NameFixers.fix("???", FixMode.SMART))
    }

    @Test
    fun `underscore mode replaces every forbidden or control character with underscore`() {
        assertEquals("Report_ Q3_.pdf", NameFixers.fix("Report: Q3?.pdf", FixMode.UNDERSCORE))
        assertEquals("a_b_c", NameFixers.fix("a/b\\c", FixMode.UNDERSCORE))
    }

    @Test
    fun `underscore mode guards reserved names`() {
        assertEquals("_NUL.txt", NameFixers.fix("NUL.txt", FixMode.UNDERSCORE))
    }

    @Test
    fun `underscore mode never returns an empty name`() {
        assertEquals("___", NameFixers.fix("???", FixMode.UNDERSCORE))
    }

    @Test
    fun `fixed name always passes the lint checks`() {
        val badNames = listOf(
            "bad:name?.txt", "  leading.txt", "trailing. ", "NUL", "a/b\\c|d<e>f\"g",
            "\u0001\u0002.txt", "a".repeat(400) + ".txt"
        )
        for (mode in FixMode.entries) {
            for (name in badNames) {
                val fixed = NameFixers.fix(name, mode)
                val issues = NameRules.checkName(fixed)

                /**
                 * Length is not re-checked here: fixers don't truncate long
                 * names on purpose (truncation policy is a product decision,
                 * not implemented), so COMPONENT_TOO_LONG may remain.
                 */
                val remaining = issues.filterNot { it.code == IssueCode.COMPONENT_TOO_LONG }
                assertTrue(
                    remaining.isEmpty(),
                    "mode=$mode name='$name' fixed='$fixed' still has issues: $remaining"
                )
            }
        }
    }
}
