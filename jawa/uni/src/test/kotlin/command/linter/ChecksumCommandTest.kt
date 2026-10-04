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
    fun `prints the root directory with its full hash`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val result = ChecksumCommand().test(dir.toString())
        assertEquals(0, result.statusCode)
        assertTrue(result.output.lineSequence().first().matches(Regex(".+/ \\([0-9a-f]{64}\\)")))
    }

    @Test
    fun `two identical trees produce the same root hash via CLI`(@TempDir dir1: Path, @TempDir dir2: Path) {
        Files.writeString(dir1.resolve("a.txt"), "hello")
        Files.writeString(dir2.resolve("a.txt"), "hello")
        val h1 = ChecksumCommand().test("$dir1 -L 0").output.substringAfterLast("(").substringBefore(")")
        val h2 = ChecksumCommand().test("$dir2 -L 0").output.substringAfterLast("(").substringBefore(")")
        assertEquals(h1, h2)
    }

    @Test
    fun `depth zero prints only the root node`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val result = ChecksumCommand().test("$dir -L 0")
        assertEquals(0, result.statusCode)
        assertEquals(1, result.output.trimEnd().lines().size)
        assertTrue(result.output.contains(dir.toString()))
        assertTrue(result.output.contains(Regex("\\([0-9a-f]{64}\\)")))
    }

    @Test
    fun `depth limits printed entries without changing root hash`(@TempDir dir: Path) {
        val subdir = Files.createDirectory(dir.resolve("subdir"))
        Files.writeString(subdir.resolve("nested.txt"), "hello")

        val full = ChecksumCommand().test(dir.toString())
        val shallow = ChecksumCommand().test("$dir -L 1")
        val deep = ChecksumCommand().test("$dir --depth 2")

        assertEquals(0, shallow.statusCode)
        assertTrue(shallow.output.contains("└── subdir/"))
        assertTrue(!shallow.output.contains("nested.txt"))
        assertTrue(deep.output.contains("nested.txt"))
        assertEquals(full.output.lineSequence().first(), shallow.output.lineSequence().first())
    }

    @Test
    fun `sorts directories and files by name and draws tree branches`(@TempDir dir: Path) {
        Files.createDirectory(dir.resolve("bdir"))
        val adir = Files.createDirectory(dir.resolve("adir"))
        Files.writeString(adir.resolve("nested.txt"), "hello")
        Files.writeString(dir.resolve("z.txt"), "hello")
        Files.writeString(dir.resolve("a.txt"), "hello")

        val lines = ChecksumCommand().test(dir.toString()).output.trimEnd().lines()
            .map { it.substringBefore(" (") }

        assertEquals(
            listOf(
                "${dir.toAbsolutePath().normalize()}/",
                "├── adir/",
                "│   └── nested.txt",
                "├── bdir/",
                "├── a.txt",
                "└── z.txt",
            ),
            lines,
        )
    }
}
