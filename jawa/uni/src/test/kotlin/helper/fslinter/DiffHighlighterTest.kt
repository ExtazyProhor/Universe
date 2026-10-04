package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class DiffHighlighterTest {
    private fun reconstruct(parts: List<DiffPart>): String = parts.joinToString("") { it.text }

    @Test
    fun `identical strings produce only EQUAL parts`() {
        val (oldParts, newParts) = DiffHighlighter.diff("same.txt", "same.txt")
        assert(oldParts.all { it.op == DiffOp.EQUAL })
        assert(newParts.all { it.op == DiffOp.EQUAL })
        assertEquals("same.txt", reconstruct(oldParts))
        assertEquals("same.txt", reconstruct(newParts))
    }

    @Test
    fun `pure insertion is all ADDED on the new side and all EQUAL on the old side`() {
        val (oldParts, newParts) = DiffHighlighter.diff("ac", "abc")
        assertEquals("ac", reconstruct(oldParts))
        assertEquals("abc", reconstruct(newParts))
        assert(oldParts.all { it.op == DiffOp.EQUAL })
        val added = newParts.filter { it.op == DiffOp.ADDED }
        assertEquals("b", added.joinToString("") { it.text })
    }

    @Test
    fun `pure deletion is all REMOVED on the old side and all EQUAL on the new side`() {
        val (oldParts, newParts) = DiffHighlighter.diff("abc", "ac")
        assertEquals("abc", reconstruct(oldParts))
        assertEquals("ac", reconstruct(newParts))
        assert(newParts.all { it.op == DiffOp.EQUAL })
        val removed = oldParts.filter { it.op == DiffOp.REMOVED }
        assertEquals("b", removed.joinToString("") { it.text })
    }

    @Test
    fun `single character replacement highlights only that character`() {
        val (oldParts, newParts) = DiffHighlighter.diff("Report: Q3.pdf", "Report- Q3.pdf")
        val removed = oldParts.filter { it.op == DiffOp.REMOVED }.joinToString("") { it.text }
        val added = newParts.filter { it.op == DiffOp.ADDED }.joinToString("") { it.text }
        assertEquals(":", removed)
        assertEquals("-", added)
    }

    @Test
    fun `completely different strings reconstruct exactly`() {
        val (oldParts, newParts) = DiffHighlighter.diff("xyz", "abc")
        assertEquals("xyz", reconstruct(oldParts))
        assertEquals("abc", reconstruct(newParts))
    }

    @Test
    fun `empty old string is all additions`() {
        val (oldParts, newParts) = DiffHighlighter.diff("", "new")
        assertEquals(0, oldParts.size)
        assertEquals(1, newParts.size)
        assertEquals("new", reconstruct(newParts))
        assert(newParts.all { it.op == DiffOp.ADDED })
    }

    @Test
    fun `empty new string is all removals`() {
        val (oldParts, newParts) = DiffHighlighter.diff("old", "")
        assertEquals(1, oldParts.size)
        assertEquals(0, newParts.size)
        assertEquals("old", reconstruct(oldParts))
        assert(oldParts.all { it.op == DiffOp.REMOVED })
    }

    @Test
    fun `both empty produces no parts`() {
        val (oldParts, newParts) = DiffHighlighter.diff("", "")
        assertEquals(0, oldParts.size)
        assertEquals(0, newParts.size)
    }
}
