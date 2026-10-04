package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.text.Normalizer
import kotlin.text.iterator

class NameRulesTest {
    @Test
    fun `clean name has no issues`() {
        assertTrue(NameRules.checkName("Report 2026.pdf").isEmpty())
        assertTrue(NameRules.checkName("Финансы").isEmpty())
    }

    @Test
    fun `empty name and dot names are flagged`() {
        assertEquals(IssueCode.EMPTY_OR_DOT, NameRules.checkName("").single().code)
        assertEquals(IssueCode.EMPTY_OR_DOT, NameRules.checkName(".").single().code)
        assertEquals(IssueCode.EMPTY_OR_DOT, NameRules.checkName("..").single().code)
    }

    @Test
    fun `forbidden characters are detected and listed`() {
        val issues = NameRules.checkName("a:b?c.txt")
        val issue = issues.single { it.code == IssueCode.FORBIDDEN_CHARS }
        assertTrue(issue.message.contains(":"))
        assertTrue(issue.message.contains("?"))
    }

    @Test
    fun `all nine forbidden characters are individually detected`() {
        for (ch in "<>:\"/\\|?*") {
            val issues = NameRules.checkName("x${ch}y")
            assertTrue(
                issues.any { it.code == IssueCode.FORBIDDEN_CHARS },
                "character '$ch' should be flagged as forbidden"
            )
        }
    }

    @Test
    fun `control characters are detected`() {
        val issues = NameRules.checkName("a\u0001b.txt")
        assertEquals(IssueCode.CONTROL_CHARS, issues.single().code)
    }

    @Test
    fun `trailing space or dot is flagged`() {
        assertEquals(IssueCode.TRAILING_SPACE_OR_DOT, NameRules.checkName("photos ").single().code)
        assertEquals(IssueCode.TRAILING_SPACE_OR_DOT, NameRules.checkName("photos.").single().code)
    }

    @Test
    fun `leading space is flagged`() {
        assertEquals(IssueCode.LEADING_SPACE, NameRules.checkName(" photos").single().code)
    }

    @Test
    fun `reserved windows device names are flagged with and without extension`() {
        for (name in listOf("NUL", "nul", "CON.txt", "com1.pdf", "LPT9")) {
            val issues = NameRules.checkName(name)
            assertTrue(
                issues.any { it.code == IssueCode.RESERVED_NAME },
                "'$name' should be flagged as reserved"
            )
        }
    }

    @Test
    fun `non reserved name that merely starts with a reserved prefix is not flagged`() {
        val issues = NameRules.checkName("NULLify.txt")
        assertFalse(issues.any { it.code == IssueCode.RESERVED_NAME })
    }

    @Test
    fun `NFD name is flagged as not NFC`() {
        val nfd = Normalizer.normalize("отчёт", Normalizer.Form.NFD)
        val issues = NameRules.checkName("$nfd.pdf")
        assertTrue(issues.any { it.code == IssueCode.NOT_NFC })
    }

    @Test
    fun `NFC name is not flagged as not NFC`() {
        val nfc = Normalizer.normalize("отчёт", Normalizer.Form.NFC)
        val issues = NameRules.checkName("$nfc.pdf")
        assertFalse(issues.any { it.code == IssueCode.NOT_NFC })
    }

    @Test
    fun `overly long component is flagged`() {
        val longName = "a".repeat(300) + ".txt"
        val issues = NameRules.checkName(longName)
        assertTrue(issues.any { it.code == IssueCode.COMPONENT_TOO_LONG })
    }

    @Test
    fun `component at the byte limit is not flagged`() {
        val name = "a".repeat(NameRules.MAX_COMPONENT_BYTES - 4) + ".txt"
        assertEquals(NameRules.MAX_COMPONENT_BYTES, name.toByteArray(Charsets.UTF_8).size)
        val issues = NameRules.checkName(name)
        assertFalse(issues.any { it.code == IssueCode.COMPONENT_TOO_LONG })
    }

    @Test
    fun `comparison key is case and NFC insensitive`() {
        val nfd = Normalizer.normalize("Отчёт.PDF", Normalizer.Form.NFD)
        val nfc = Normalizer.normalize("отчёт.pdf", Normalizer.Form.NFC)
        assertEquals(NameRules.comparisonKey(nfd), NameRules.comparisonKey(nfc))
    }

    @Test
    fun `a name can trigger multiple issues at once`() {
        val issues = NameRules.checkName("bad:name ")
        val codes = issues.map { it.code }.toSet()
        assertTrue(IssueCode.FORBIDDEN_CHARS in codes)
        assertTrue(IssueCode.TRAILING_SPACE_OR_DOT in codes)
    }
}
