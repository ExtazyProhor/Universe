package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class ChecksumCommandTest {
    @Test
    fun `prints a root hash line`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val result = ChecksumCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(Regex("ROOT HASH: [0-9a-f]{64}").containsMatchIn(result.output))
    }

    @Test
    fun `two identical trees produce the same root hash via CLI`(@TempDir dir1: Path, @TempDir dir2: Path) {
        Files.writeString(dir1.resolve("a.txt"), "hello")
        Files.writeString(dir2.resolve("a.txt"), "hello")
        val h1 = ChecksumCommand().test("$dir1 --quiet").output
        val h2 = ChecksumCommand().test("$dir2 --quiet").output
        assertEquals(h1, h2)
    }

    @Test
    fun `quiet mode omits the tree listing`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val result = ChecksumCommand().test("$dir --quiet")
        assertTrue(!result.output.contains("[file]"))
    }
}
