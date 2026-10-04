package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class FixCommandTest {
    @Test
    fun `dry run does not rename anything`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("bad:name.txt"))
        val result = FixCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(Files.exists(dir.resolve("bad:name.txt")))
        assertTrue(result.output.contains("dry run"))
    }

    @Test
    fun `apply renames the file on disk`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("bad:name.txt"))
        val result = FixCommand().test("$dir --apply")
        assertEquals(0, result.statusCode)
        assertTrue(Files.exists(dir.resolve("bad-name.txt")))
        assertTrue(!Files.exists(dir.resolve("bad:name.txt")))
    }

    @Test
    fun `underscore mode can be selected explicitly`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("bad:name.txt"))
        val result = FixCommand().test("$dir --mode underscore --apply")
        assertEquals(0, result.statusCode)
        assertTrue(Files.exists(dir.resolve("bad_name.txt")))
    }

    @Test
    fun `clean tree has nothing to fix`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("report.pdf"))
        val result = FixCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(result.output.contains("Nothing to fix"))
    }
}
