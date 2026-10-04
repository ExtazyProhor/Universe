package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path

/**
 * FixPlanner only reads Path metadata (parent/filename), so these tests use
 * synthetic, non-existent paths -- no real filesystem needed
 */
class FixPlannerTest {
    private fun entry(path: String, isDir: Boolean = false, depth: Int = 0): FileSystemEntry {
        val p = Path.of(path)
        return FileSystemEntry(p, Path.of(p.fileName.toString()), isDir, depth)
    }

    @Test
    fun `clean names produce no plan items`() {
        val entries = listOf(entry("/root/Report.pdf"), entry("/root/photos", isDir = true))
        assertTrue(FixPlanner.plan(entries, FixMode.SMART).isEmpty())
    }

    @Test
    fun `a single bad name is fixed without a suffix`() {
        val entries = listOf(entry("/root/Report:Q3.pdf"))
        val plan = FixPlanner.plan(entries, FixMode.SMART)
        assertEquals(1, plan.size)
        assertEquals("Report-Q3.pdf", plan.single().newName)
    }

    @Test
    fun `collision with an existing clean sibling gets a numeric suffix`() {
        // "Report.pdf" already exists; "Report?.pdf" would also fix to "Report.pdf"
        val entries = listOf(
            entry("/root/Report.pdf"),
            entry("/root/Report?.pdf"),
        )
        val plan = FixPlanner.plan(entries, FixMode.SMART)
        assertEquals(1, plan.size)
        assertEquals("Report (2).pdf", plan.single().newName)
    }

    /**
     * Both `:` and `|` map to `-` in smart mode, so these two different source names collide
     * on the same fixed name `a-1.txt`.
     * (Using `|` rather than `/` here since `/` would be parsed as a path separator by `Path.of(...)`,
     * not as part of the file name)
     */
    @Test
    fun `collision between two fixed siblings gets suffixes in order`() {
        val entries = listOf(
            entry("/root/a:1.txt"),
            entry("/root/a|1.txt"),
        )
        val plan = FixPlanner.plan(entries, FixMode.SMART)
        val names = plan.map { it.newName }.toSet()
        assertEquals(2, plan.size)
        assertTrue("a-1.txt" in names)
        assertTrue("a-1 (2).txt" in names)
    }

    @Test
    fun `collisions are scoped per parent directory`() {
        val entries = listOf(
            entry("/root/a/x:1.txt"),
            entry("/root/b/x|1.txt"),
        )
        val plan = FixPlanner.plan(entries, FixMode.SMART)
        // Different parents, same fixed name is fine, no suffix needed.
        assertTrue(plan.all { it.newName == "x-1.txt" })
    }

    @Test
    fun `underscore mode plan matches underscore fixer`() {
        val entries = listOf(entry("/root/a/b.txt"), entry("/root/a:b.txt"))
        val plan = FixPlanner.plan(entries, FixMode.UNDERSCORE)
        assertEquals(1, plan.size)
        assertEquals("a_b.txt", plan.single().newName)
    }
}
