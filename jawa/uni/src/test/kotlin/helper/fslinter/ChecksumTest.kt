package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.writeText

class ChecksumTest {
    private fun makeTree(dir: Path) {
        dir.resolve("sub").createDirectory()
        dir.resolve("a.txt").writeText("hello")
        dir.resolve("sub/b.txt").writeText("world")
    }

    @Test
    fun `identical trees produce identical root hash`(@TempDir dir1: Path, @TempDir dir2: Path) {
        val expectedHash = "1e8640578722936371dc632843bc9c518becc43c51550d188c62e8cf065ecaa9"
        makeTree(dir1)
        makeTree(dir2)
        val hash1 = Checksum.build(dir1).rootHash
        val hash2 = Checksum.build(dir2).rootHash
        assertEquals(hash1, hash2)
        assertEquals(expectedHash, hash1)
    }

    @Test
    fun `root hash is independent of filesystem creation order`(@TempDir dir1: Path, @TempDir dir2: Path) {
        Files.writeString(dir1.resolve("a.txt"), "1")
        Files.writeString(dir1.resolve("b.txt"), "2")

        Files.writeString(dir2.resolve("b.txt"), "2")
        Files.writeString(dir2.resolve("a.txt"), "1")

        assertEquals(Checksum.build(dir1).rootHash, Checksum.build(dir2).rootHash)
    }

    @Test
    fun `changing file content changes the root hash`(@TempDir dir: Path) {
        makeTree(dir)
        val before = Checksum.build(dir).rootHash
        Files.writeString(dir.resolve("a.txt"), "changed")
        val after = Checksum.build(dir).rootHash
        assertNotEquals(before, after)
    }

    @Test
    fun `renaming a file changes the root hash`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val before = Checksum.build(dir).rootHash
        Files.move(dir.resolve("a.txt"), dir.resolve("b.txt"))
        val after = Checksum.build(dir).rootHash
        assertNotEquals(before, after)
    }

    @Test
    fun `garbage files do not affect the root hash`(@TempDir dir: Path) {
        Files.writeString(dir.resolve("a.txt"), "hello")
        val before = Checksum.build(dir).rootHash
        Files.createFile(dir.resolve(".DS_Store"))
        Files.createFile(dir.resolve("Thumbs.db"))
        val after = Checksum.build(dir).rootHash
        assertEquals(before, after)
    }

    @Test
    fun `empty directory has a stable, deterministic hash`(@TempDir dir1: Path, @TempDir dir2: Path) {
        val expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val hash1 = Checksum.build(dir1).rootHash
        val hash2 = Checksum.build(dir2).rootHash
        assertEquals(hash1, hash2)
        assertEquals(expectedHash, hash1)
    }

    @Test
    fun `hashFile is deterministic and content-dependent`(@TempDir dir: Path) {
        val f1 = dir.resolve("a.txt").also { Files.writeString(it, "same") }
        val f2 = dir.resolve("b.txt").also { Files.writeString(it, "same") }
        val f3 = dir.resolve("c.txt").also { Files.writeString(it, "different") }
        assertEquals(Checksum.hashFile(f1), Checksum.hashFile(f2))
        assertNotEquals(Checksum.hashFile(f1), Checksum.hashFile(f3))
    }

    @Test
    fun `tree structure exposes files and folders with relative paths`(@TempDir dir: Path) {
        makeTree(dir)
        val result = Checksum.build(dir)
        val byRel = result.tree.flatten().associateBy { it.rel }
        assert(byRel.containsKey("a.txt"))
        assert(byRel.containsKey("sub"))
        assert(byRel.getValue("sub").children!!.single().rel == "sub/b.txt")
    }

    private fun List<ChecksumNode>.flatten(): List<ChecksumNode> {
        return this + this.flatMap { it.children?.flatten() ?: emptyList() }
    }
}
