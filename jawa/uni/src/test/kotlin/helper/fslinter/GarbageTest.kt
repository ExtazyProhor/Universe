package ru.prohor.universe.uni.cli.helper.fslinter

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GarbageTest {
    @Test
    fun `known macOS junk files are detected`() {
        assertTrue(Garbage.isGarbage(".DS_Store", false))
        assertTrue(Garbage.isGarbage("._report.pdf", false))
        assertTrue(Garbage.isGarbage(".apdisk", false))
    }

    @Test
    fun `known macOS junk folders are detected`() {
        assertTrue(Garbage.isGarbage(".Spotlight-V100", true))
        assertTrue(Garbage.isGarbage(".fseventsd", true))
        assertTrue(Garbage.isGarbage(".Trashes", true))
    }

    @Test
    fun `known Windows junk files are detected`() {
        assertTrue(Garbage.isGarbage("Thumbs.db", false))
        assertTrue(Garbage.isGarbage("desktop.ini", false))
    }

    @Test
    fun `known Windows junk folders are detected`() {
        assertTrue(Garbage.isGarbage($$"$RECYCLE.BIN", true))
        assertTrue(Garbage.isGarbage("System Volume Information", true))
    }

    @Test
    fun `a regular file is never garbage`() {
        assertFalse(Garbage.isGarbage("Report.pdf", false))
        // same name, but as a folder -- not recognized
        assertFalse(Garbage.isGarbage("Thumbs.db", true))
    }

    @Test
    fun `AppleDouble prefix only matches at the start of the name`() {
        assertTrue(Garbage.isGarbage("._x", false))
        assertFalse(Garbage.isGarbage("a._x", false))
    }
}
