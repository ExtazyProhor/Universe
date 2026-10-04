package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class CleanCommandTest {
    @Test
    fun `no junk reports nothing to do`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("report.pdf"))
        val result = CleanCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(result.output.contains("No junk files found"))
    }

    @Test
    fun `dry run lists junk but does not delete it`(@TempDir dir: Path) {
        Files.createFile(dir.resolve(".DS_Store"))
        val result = CleanCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(result.output.contains(".DS_Store"))
        assertTrue(Files.exists(dir.resolve(".DS_Store")))
    }

    @Test
    fun `apply deletes junk files`(@TempDir dir: Path) {
        Files.createFile(dir.resolve(".DS_Store"))
        Files.createFile(dir.resolve("Thumbs.db"))
        Files.createFile(dir.resolve("report.pdf"))
        val result = CleanCommand().test("$dir --apply")
        assertEquals(0, result.statusCode)
        assertTrue(!Files.exists(dir.resolve(".DS_Store")))
        assertTrue(!Files.exists(dir.resolve("Thumbs.db")))
        assertTrue(Files.exists(dir.resolve("report.pdf")))
    }

    @Test
    fun `apply removes a junk folder recursively`(@TempDir dir: Path) {
        val recycle = Files.createDirectory(dir.resolve($$"$RECYCLE.BIN"))
        Files.createFile(recycle.resolve("inner.dat"))
        val result = CleanCommand().test("$dir --apply")
        assertEquals(0, result.statusCode)
        assertTrue(!Files.exists(recycle))
    }
}
