package ru.prohor.universe.padawan.college;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.padawan.college.des.Des;

public class DesTest {
    private Des des;

    @BeforeEach
    void setUp() {
        des = Des.getInstance();
    }

    @Test
    void testGetInstanceNotNull() {
        Assertions.assertNotNull(des, "getInstance must return not null object");
    }

    @Test
    void testGenerateKeyValid() {
        long key = des.generateKey();
        Assertions.assertTrue(key != 0, "Key mustn't be equals 0");
    }

    @Test
    void testGenerateKeyUnique() {
        long key1 = des.generateKey();
        long key2 = des.generateKey();
        Assertions.assertNotEquals(key1, key2, "Successive calls must generate different keys");
    }

    @Test
    void testEncodeDecodeReversible() {
        String original = "Hello, World!";
        long key = des.generateKey();

        String encoded = des.encode(original, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(original, decoded, "The encoded message must match the original");
    }

    @Test
    void testEncodeDifferentKeys() {
        String message = "Test message";
        long key1 = des.generateKey();
        long key2 = des.generateKey();

        String encoded1 = des.encode(message, key1);
        String encoded2 = des.encode(message, key2);

        Assertions.assertNotEquals(encoded1, encoded2, "Different keys should give different results");
    }

    @Test
    void testDecodeWrongKey() {
        String original = "Secret message";
        long correctKey = des.generateKey();
        long wrongKey = des.generateKey();

        String encoded = des.encode(original, correctKey);
        String decoded = des.decode(encoded, wrongKey);

        Assertions.assertNotEquals(original, decoded, "An incorrect key should not decode the message");
    }

    @Test
    void testEmptyString() {
        String empty = "";
        long key = des.generateKey();

        String encoded = des.encode(empty, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(empty, decoded, "An empty string should be handled correctly");
    }

    @Test
    void testLongText() {
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.".repeat(10);
        long key = des.generateKey();

        String encoded = des.encode(longText, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(longText, decoded, "Long text must be encoded correctly");
    }

    @Test
    void testSpecialCharacters() {
        String special = "!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";
        long key = des.generateKey();

        String encoded = des.encode(special, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(special, decoded, "Special characters must be handled correctly");
    }

    @Test
    void testUnicodeCharacters() {
        String unicode = "Привет мир! 你好世界 🌍🚀";
        long key = des.generateKey();

        String encoded = des.encode(unicode, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(unicode, decoded, "Unicode characters should be handled correctly");
    }

    @Test
    void testNumericString() {
        String numbers = "1234567890";
        long key = des.generateKey();

        String encoded = des.encode(numbers, key);
        String decoded = des.decode(encoded, key);

        Assertions.assertEquals(numbers, decoded, "Numeric strings should be handled correctly");
    }

    @Test
    void testEncodedDifferentFromOriginal() {
        String original = "Test";
        long key = des.generateKey();

        String encoded = des.encode(original, key);

        Assertions.assertNotEquals(original, encoded, "The encoded message must be different from the original");
    }

    @Test
    void testConsistentEncoding() {
        String message = "Consistent test";
        long key = 12345L;

        String encoded1 = des.encode(message, key);
        String encoded2 = des.encode(message, key);

        Assertions.assertEquals(encoded1, encoded2, "The same input data should produce the same output");
    }

    @Test
    void testNullMessage() {
        long key = des.generateKey();

        Assertions.assertThrows(
                Exception.class, () -> des.encode(null, key), "encode with null must throw exception"
        );
    }

    @Test
    void testMultipleEncodeDecode() {
        String original = "Multiple cycles test";
        long key = des.generateKey();

        String result = original;
        for (int i = 0; i < 5; i++) {
            result = des.encode(result, key);
            result = des.decode(result, key);
        }

        Assertions.assertEquals(original, result, "Multiple cycles must preserve data");
    }
}
