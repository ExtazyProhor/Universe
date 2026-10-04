package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.text.Normalizer

class DuplicatesTest {
    private fun entry(path: String): FileSystemEntry {
        val p = Path.of(path)
        return FileSystemEntry(p, Path.of(p.fileName.toString()), false, 0)
    }

    @Test
    fun `no duplicates among distinct names`() {
        val entries = listOf(entry("/root/a.txt"), entry("/root/b.txt"))
        assertTrue(Duplicates.findGroups(entries).isEmpty())
    }

    @Test
    fun `case insensitive duplicate in the same folder is found`() {
        val entries = listOf(entry("/root/Report.pdf"), entry("/root/report.PDF"))
        val groups = Duplicates.findGroups(entries)
        assertEquals(1, groups.size)
        assertEquals(2, groups.single().size)
    }

    @Test
    fun `same name in different folders is not a duplicate`() {
        val entries = listOf(entry("/root/a/Report.pdf"), entry("/root/b/Report.pdf"))
        assertTrue(Duplicates.findGroups(entries).isEmpty())
    }

    @Test
    fun `NFC and NFD forms of the same name are detected as duplicates`() {
        val nfc = Normalizer.normalize("отчёт.pdf", Normalizer.Form.NFC)
        val nfd = Normalizer.normalize("отчёт.pdf", Normalizer.Form.NFD)
        val entries = listOf(entry("/root/$nfc"), entry("/root/$nfd"))
        val groups = Duplicates.findGroups(entries)
        assertEquals(1, groups.size)
    }

    @Test
    fun `group of three is reported as one group with three entries`() {
        val entries = listOf(
            entry("/root/X.txt"),
            entry("/root/x.txt"),
            entry("/root/X.TXT"))
        val groups = Duplicates.findGroups(entries)
        assertEquals(1, groups.size)
        assertEquals(3, groups.single().size)
    }
}
