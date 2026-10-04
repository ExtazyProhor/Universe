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
    fun `files with the same issue share one description and keep their details`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("bad:name.txt"))
        Files.createFile(dir.resolve("bad?name.txt"))
        val result = LintCommand().test(dir.toString())
        assertEquals(1, result.statusCode)
        assertTrue(result.output.contains("Forbidden characters (2)"))
        val description = "Name contains characters forbidden on Windows."
        assertEquals(1, Regex(Regex.escape(description)).findAll(result.output).count())
        assertTrue(result.output.contains("bad:name.txt  —  :"))
        assertTrue(result.output.contains("bad?name.txt  —  ?"))
    }

    @Test
    fun `garbage files are not reported`(@TempDir dir: Path) {
        Files.createFile(dir.resolve(".DS_Store"))
        val result = LintCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
    }
}
