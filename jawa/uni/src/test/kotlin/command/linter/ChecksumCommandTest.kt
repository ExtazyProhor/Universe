package ru.prohor.universe.uni.cli.command.linter

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import ru.prohor.universe.uni.cli.helper.fslinter.Checksum
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

    @Test
    fun `duplicates groups files by content with relative paths`(@TempDir dir: Path) {
        val nested = Files.createDirectory(dir.resolve("nested"))
        val first = Files.writeString(dir.resolve("a.txt"), "same")
        Files.writeString(nested.resolve("b.txt"), "same")
        Files.writeString(dir.resolve("unique.txt"), "different")

        val result = ChecksumCommand().test("$dir --duplicates -L 0")

        assertEquals(0, result.statusCode)
        assertEquals("${Checksum.hashFile(first)}\n  a.txt\n  nested/b.txt\n", result.output)
    }

    @Test
    fun `duplicates prints every group and omits files without a match`(@TempDir dir: Path) {
        val first = Files.writeString(dir.resolve("a.txt"), "first")
        Files.writeString(dir.resolve("b.txt"), "first")
        val second = Files.writeString(dir.resolve("c.txt"), "second")
        Files.writeString(dir.resolve("d.txt"), "second")
        Files.writeString(dir.resolve("unique.txt"), "unique")

        val result = ChecksumCommand().test("$dir --duplicates")
        val expected = mapOf(
            Checksum.hashFile(first) to listOf("a.txt", "b.txt"),
            Checksum.hashFile(second) to listOf("c.txt", "d.txt"),
        ).toSortedMap().entries.joinToString("\n") { (hash, paths) ->
            "$hash\n${paths.joinToString("") { "  $it\n" }}"
        }

        assertEquals(0, result.statusCode)
        assertEquals(expected, result.output)
    }

    @Test
    fun `duplicates reports when all files are unique`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "first")
        Files.writeString(dir.resolve("b.txt"), "second")

        val result = ChecksumCommand().test("$dir --duplicates")

        assertEquals(0, result.statusCode)
        assertEquals("No duplicates found\n", result.output)
    }
}
