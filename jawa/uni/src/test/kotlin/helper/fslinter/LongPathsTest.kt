package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path

class LongPathsTest {
    private fun entry(rel: String) = FileSystemEntry(Path.of("/abs", rel), Path.of(rel), false, 0)

    @Test
    fun `short path is not flagged`() {
        val entries = listOf(entry("docs/report.pdf"))
        assertTrue(LongPaths.find(entries, prefixLength = 0).isEmpty())
    }

    @Test
    fun `path exceeding the limit is flagged with the correct total length`() {
        val rel = "a".repeat(300)
        val entries = listOf(entry(rel))
        val result = LongPaths.find(entries, prefixLength = 0)
        assertEquals(1, result.size)
        assertEquals(300, result.single().second)
    }

    @Test
    fun `prefix length pushes a borderline path over the limit`() {
        val rel = "a".repeat(250) // alone under 260
        val entries = listOf(entry(rel))
        assertTrue(LongPaths.find(entries, prefixLength = 0).isEmpty())
        val withPrefix = LongPaths.find(entries, prefixLength = 20)
        assertEquals(1, withPrefix.size)
    }

    @Test
    fun `custom limit is respected`() {
        val entries = listOf(entry("a".repeat(50)))
        assertTrue(LongPaths.find(entries, prefixLength = 0, limit = 40).isNotEmpty())
        assertTrue(LongPaths.find(entries, prefixLength = 0, limit = 100).isEmpty())
    }
}
