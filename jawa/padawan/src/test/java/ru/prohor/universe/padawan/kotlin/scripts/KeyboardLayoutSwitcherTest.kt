package ru.prohor.universe.padawan.kotlin.scripts

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KeyboardLayoutSwitcherTest {
    @Test
    fun testAppleLatinToRusRow1() {
        val string = "`1234567890-="
        val expected = "]1234567890-="
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow1Shift() {
        val string = "~!@#$%^&*()_+"
        val expected = "[!\"№%:,.;()_+"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow2() {
        val string = "qwertyuiop[]\\"
        val expected = "йцукенгшщзхъё"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow2Shift() {
        val string = "QWERTYUIOP{}"
        val expected = "ЙЦУКЕНГШЩЗХЪ"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow3() {
        val string = "asdfghjkl;'"
        val expected = "фывапролджэ"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow3Shift() {
        val string = "ASDFGHJKL:\""
        val expected = "ФЫВАПРОЛДЖЭ"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow4() {
        val string = "zxcvbnm,./"
        val expected = "ячсмитьбю/"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleLatinToRusRow4Shift() {
        val string = "ZXCVBNM<>?"
        val expected = "ЯЧСМИТЬБЮ?"
        val actual = convert(string, KeyboardLayout.APPLE_LATIN_TO_RUS)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow1() {
        val string = "]1234567890-="
        val expected = "`1234567890-="
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow1Shift() {
        val string = "[!\"№%:,.;()_+"
        val expected = "~!@#$%^&*()_+"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow2() {
        val string = "йцукенгшщзхъё"
        val expected = "qwertyuiop[]\\"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow2Shift() {
        val string = "ЙЦУКЕНГШЩЗХЪ"
        val expected = "QWERTYUIOP{}"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow3() {
        val string = "фывапролджэ"
        val expected = "asdfghjkl;'"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow3Shift() {
        val string = "ФЫВАПРОЛДЖЭ"
        val expected = "ASDFGHJKL:\""
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow4() {
        val string = "ячсмитьбю/"
        val expected = "zxcvbnm,./"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }

    @Test
    fun testAppleRusToLatinRow4Shift() {
        val string = "ЯЧСМИТЬБЮ?"
        val expected = "ZXCVBNM<>?"
        val actual = convert(string, KeyboardLayout.APPLE_RUS_TO_LATIN)
        assertEquals(expected, actual)
    }
}
