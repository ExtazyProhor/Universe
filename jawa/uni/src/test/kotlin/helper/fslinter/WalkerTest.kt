package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class WalkerTest {
    @Test
    fun `walk finds files and folders at correct depth`(@TempDir dir: Path) {
        Files.createFile(dir.resolve("a.txt"))
        val sub = Files.createDirectory(dir.resolve("sub"))
        Files.createFile(sub.resolve("b.txt"))

        val entries = Walker.walk(dir).associateBy { it.rel.toString().replace('\\', '/') }

        assertEquals(0, entries.getValue("a.txt").depth)
        assertEquals(0, entries.getValue("sub").depth)
        assertTrue(entries.getValue("sub").isDir)
        assertEquals(1, entries.getValue("sub/b.txt").depth)
        assertFalse(entries.getValue("sub/b.txt").isDir)
    }

    @Test
    fun `walk skips garbage files and does not descend into garbage folders`(@TempDir dir: Path) {
        Files.createFile(dir.resolve(".DS_Store"))
        val recycle = Files.createDirectory(dir.resolve($$"$RECYCLE.BIN"))
        Files.createFile(recycle.resolve("inner.dat"))
        Files.createFile(dir.resolve("real.txt"))

        val names = Walker.walk(dir).map { it.path.fileName.toString() }

        assertTrue("real.txt" in names)
        assertFalse(".DS_Store" in names)
        assertFalse($$"$RECYCLE.BIN" in names)
        assertFalse("inner.dat" in names)
    }

    @Test
    fun `walk skips symlinks`(@TempDir dir: Path) {
        val real = Files.createFile(dir.resolve("real.txt"))
        try {
            Files.createSymbolicLink(dir.resolve("link.txt"), real)
        } catch (_: UnsupportedOperationException) {
            return
        } catch (_: IOException) {
            return
        }

        val names = Walker.walk(dir).map { it.path.fileName.toString() }
        assertTrue("real.txt" in names)
        assertFalse("link.txt" in names)
    }

    @Test
    fun `findGarbage reports a junk folder as one entry without descending`(@TempDir dir: Path) {
        val recycle = Files.createDirectory(dir.resolve($$"$RECYCLE.BIN"))
        Files.createFile(recycle.resolve("inner.dat"))
        Files.createFile(dir.resolve(".DS_Store"))
        Files.createFile(dir.resolve("real.txt"))

        val garbage = Walker.findGarbage(dir)
        val names = garbage.map { it.path.fileName.toString() }.toSet()

        assertEquals(setOf($$"$RECYCLE.BIN", ".DS_Store"), names)
    }

    @Test
    fun `empty directory yields no entries`(@TempDir dir: Path) {
        assertTrue(Walker.walk(dir).isEmpty())
    }
}
