package ru.prohor.universe.jocasta.core.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringExtensionsTest {
    @Test
    void testEscapeBackslash() {
        String input = "\\";
        String expected = "\\\\";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEscapeNewline() {
        String input = "line1\nline2";
        String expected = "line1\\nline2";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEscapeCarriageReturn() {
        String input = "line1\rline2";
        String expected = "line1\\rline2";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEscapeTab() {
        String input = "word1\tword2";
        String expected = "word1\\tword2";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEscapeFormFeed() {
        String input = "page1\fpage2";
        String expected = "page1\\fpage2";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEscapeBackspace() {
        String input = "abc\bdef";
        String expected = "abc\\bdef";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testNoEscapeCharacters() {
        String input = "HelloWorld123";
        Assertions.assertEquals(input, StringExtensions.escape(input));
    }

    @Test
    void testMixedCharacters() {
        String input = "a\\b\nc\rd\t";
        String expected = "a\\\\b\\nc\\rd\\t";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }

    @Test
    void testEmptyString() {
        String input = "";
        String expected = "";
        Assertions.assertEquals(expected, StringExtensions.escape(input));
    }
}
