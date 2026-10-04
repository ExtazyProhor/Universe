package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class LintCommandTest {
    @Test
    fun `clean tree exits 0 and reports no problems`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("report.pdf"))
        val result = LintCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(result.output.contains("No problems found"))
    }

    @Test
    fun `tree with a bad name exits 1 and lists the issue`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("bad:name.txt"))
        val result = LintCommand().test(dir.toString())
        assertEquals(1, result.statusCode)
        assertTrue(result.output.contains("FORBIDDEN_CHARS"))
    }

    @Test
    fun `garbage files are not reported`(@TempDir dir: Path) {
        Files.createFile(dir.resolve(".DS_Store"))
        val result = LintCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
    }
}
