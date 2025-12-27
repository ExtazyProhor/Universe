package ru.prohor.universe.jocasta.core.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringExtensionsTest {
    /**
     * Tests for <code>escape()</code>
     */
    @Test
    void testEscape_EmptyString() {
        Assertions.assertEquals("", StringExtensions.escape(""));
    }

    @Test
    void testEscape_NoSpecialCharacters() {
        Assertions.assertEquals("Hello World", StringExtensions.escape("Hello World"));
        Assertions.assertEquals("abc123", StringExtensions.escape("abc123"));
    }

    @Test
    void testEscape_Backslash() {
        Assertions.assertEquals("\\\\", StringExtensions.escape("\\"));
        Assertions.assertEquals("path\\\\to\\\\file", StringExtensions.escape("path\\to\\file"));
    }

    @Test
    void testEscape_Newline() {
        Assertions.assertEquals("\\n", StringExtensions.escape("\n"));
        Assertions.assertEquals("line1\\n line2", StringExtensions.escape("line1\n line2"));
    }

    @Test
    void testEscape_CarriageReturn() {
        Assertions.assertEquals("\\r", StringExtensions.escape("\r"));
        Assertions.assertEquals("text\\r more", StringExtensions.escape("text\r more"));
    }

    @Test
    void testEscape_Tab() {
        Assertions.assertEquals("\\t", StringExtensions.escape("\t"));
        Assertions.assertEquals("col1\\t col2", StringExtensions.escape("col1\t col2"));
    }

    @Test
    void testEscape_FormFeed() {
        Assertions.assertEquals("\\f", StringExtensions.escape("\f"));
    }

    @Test
    void testEscape_Backspace() {
        Assertions.assertEquals("\\b", StringExtensions.escape("\b"));
    }

    @Test
    void testEscape_MultipleSpecialCharacters() {
        Assertions.assertEquals("\\n\\t\\r", StringExtensions.escape("\n\t\r"));
        Assertions.assertEquals("\\\\n\\\\t", StringExtensions.escape("\\n\\t"));
    }

    @Test
    void testEscape_MixedContent() {
        Assertions.assertEquals("Hello\\nWorld\\t!", StringExtensions.escape("Hello\nWorld\t!"));
        Assertions.assertEquals("Path: C:\\\\Users\\\\Name", StringExtensions.escape("Path: C:\\Users\\Name"));
    }

    @Test
    void testEscape_AllEscapeCharacters() {
        String input = "\\\n\r\t\f\b";
        String expected = "\\\\\\n\\r\\t\\f\\b";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    /**
     * Tests for <code>fillToLength()</code>
     */
    @Test
    void testFillToLength_AlreadyLongEnough() {
        Assertions.assertEquals("12345", StringExtensions.fillToLength("12345", '0', 5));
        Assertions.assertEquals("12345", StringExtensions.fillToLength("12345", '0', 3));
    }

    @Test
    void testFillToLength_EmptyString() {
        Assertions.assertEquals("000", StringExtensions.fillToLength("", '0', 3));
    }

    @Test
    void testFillToLength_AddOneCharacter() {
        Assertions.assertEquals("01", StringExtensions.fillToLength("1", '0', 2));
    }

    @Test
    void testFillToLength_AddMultipleCharacters() {
        Assertions.assertEquals("00042", StringExtensions.fillToLength("42", '0', 5));
        Assertions.assertEquals("   hello", StringExtensions.fillToLength("hello", ' ', 8));
    }

    @Test
    void testFillToLength_DifferentFillCharacters() {
        Assertions.assertEquals("###test", StringExtensions.fillToLength("test", '#', 7));
        Assertions.assertEquals("___abc", StringExtensions.fillToLength("abc", '_', 6));
    }

    @Test
    void testFillToLength_TargetZero() {
        Assertions.assertEquals("abc", StringExtensions.fillToLength("abc", '0', 0));
    }

    @Test
    void testFillToLength_TargetNegative() {
        Assertions.assertEquals("abc", StringExtensions.fillToLength("abc", '0', -5));
    }

    @Test
    void testFillToLength_ExactLength() {
        Assertions.assertEquals("123", StringExtensions.fillToLength("123", '0', 3));
    }

    /**
     * Tests for <code>utf8Length()</code>
     */
    @Test
    void testUtf8Length_EmptyString() {
        Assertions.assertEquals(0, StringExtensions.utf8Length(""));
    }

    @Test
    void testUtf8Length_AsciiOnly() {
        Assertions.assertEquals(5, StringExtensions.utf8Length("Hello"));
        Assertions.assertEquals(13, StringExtensions.utf8Length("Hello, World!"));
    }

    @Test
    void testUtf8Length_SingleByteCharacters() {
        // Characters in range 0x00-0x7F (1 byte in UTF-8)
        Assertions.assertEquals(1, StringExtensions.utf8Length("A"));
        Assertions.assertEquals(1, StringExtensions.utf8Length("z"));
        Assertions.assertEquals(1, StringExtensions.utf8Length("0"));
    }

    @Test
    void testUtf8Length_TwoByteCharacters() {
        // Characters in range 0x80-0x7FF (2 bytes in UTF-8)
        Assertions.assertEquals(2, StringExtensions.utf8Length("ñ")); // U+00F1
        Assertions.assertEquals(2, StringExtensions.utf8Length("ö")); // U+00F6
        Assertions.assertEquals(2, StringExtensions.utf8Length("Ñ")); // U+00D1
        Assertions.assertEquals(5, StringExtensions.utf8Length("café"));
        // 4 + 2 = 6 bytes: c(1) + a(1) + f(1) + é(2) + actual is 5 chars
    }

    @Test
    void testUtf8Length_ThreeByteCharacters() {
        // Characters in range 0x800-0xFFFF (3 bytes in UTF-8)
        Assertions.assertEquals(3, StringExtensions.utf8Length("€")); // U+20AC
        Assertions.assertEquals(3, StringExtensions.utf8Length("中")); // U+4E2D
        Assertions.assertEquals(6, StringExtensions.utf8Length("日本"));
    }

    @Test
    void testUtf8Length_FourByteCharacters_SurrogatePairs() {
        // Characters beyond U+FFFF (4 bytes in UTF-8, represented as surrogate pairs in Java)
        Assertions.assertEquals(4, StringExtensions.utf8Length("😀")); // U+1F600 (Grinning Face)
        Assertions.assertEquals(4, StringExtensions.utf8Length("🎉")); // U+1F389 (Party Popper)
        Assertions.assertEquals(4, StringExtensions.utf8Length("𝕳")); // U+1D573 (Mathematical Double-Struck H)
    }

    @Test
    void testUtf8Length_MixedCharacters() {
        String mixed = "A" // 1 byte
                + "ñ" // 2 bytes
                + "中" // 3 bytes
                + "😀"; // 4 bytes
        Assertions.assertEquals(10, StringExtensions.utf8Length(mixed));
    }

    @Test
    void testUtf8Length_MultipleEmojis() {
        Assertions.assertEquals(8, StringExtensions.utf8Length("😀😀"));
        Assertions.assertEquals(12, StringExtensions.utf8Length("🎉🎊🎈"));
    }

    @Test
    void testUtf8Length_CyrillicCharacters() {
        // Cyrillic characters are 2 bytes in UTF-8
        Assertions.assertEquals(12, StringExtensions.utf8Length("Привет"));
    }

    @Test
    void testUtf8Length_MixedLanguages() {
        String text = "Hello世界😀"; // Hello(5×1) + 世界(2×3) + 😀(1×4)
        Assertions.assertEquals(15, StringExtensions.utf8Length(text));
    }

    @Test
    void testUtf8Length_SpecialCharacters() {
        Assertions.assertEquals(1, StringExtensions.utf8Length(" ")); // Space
        Assertions.assertEquals(1, StringExtensions.utf8Length("\n")); // Newline
        Assertions.assertEquals(1, StringExtensions.utf8Length("\t")); // Tab
    }

    @Test
    void testUtf8Length_NumbersAndSymbols() {
        Assertions.assertEquals(10, StringExtensions.utf8Length("0123456789"));
        Assertions.assertEquals(5, StringExtensions.utf8Length("!@#$%"));
    }

    @Test
    void testUtf8Length_LongString() {
        String longAscii = "a".repeat(1000);
        Assertions.assertEquals(1000, StringExtensions.utf8Length(longAscii));

        String longUnicode = "中".repeat(100);
        Assertions.assertEquals(300, StringExtensions.utf8Length(longUnicode));
    }

    @Test
    void testUtf8Length_EdgeCaseBoundaries() {
        Assertions.assertEquals(1, StringExtensions.utf8Length("\u007F"));  // Max 1-byte
        Assertions.assertEquals(2, StringExtensions.utf8Length("\u0080"));  // Min 2-byte
        Assertions.assertEquals(2, StringExtensions.utf8Length("\u07FF"));  // Max 2-byte
        Assertions.assertEquals(3, StringExtensions.utf8Length("\u0800"));  // Min 3-byte
        Assertions.assertEquals(3, StringExtensions.utf8Length("\uFFFF"));  // Max BMP
    }
}
