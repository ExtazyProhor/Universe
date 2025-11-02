package ru.prohor.universe.padawan.college;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.prohor.universe.padawan.college.crypto.Cypher;

import java.util.stream.Stream;

public class CypherTest {
    @ParameterizedTest
    @MethodSource("provideValidMessagesForAllCyphers")
    void testEncodeDecodeReturnsOriginalMessage(Cypher cypher, String message, String key) {
        String encoded = cypher.encode(message, key);
        String decoded = cypher.decode(encoded, key);
        Assertions.assertEquals(message, decoded);
    }

    @ParameterizedTest
    @EnumSource(Cypher.class)
    void testEmptyMessage(Cypher cypher) {
        String key = "ключ";
        String result = cypher.encode("", key);
        Assertions.assertNotNull(result);
    }

    @ParameterizedTest
    @MethodSource("provideCyrillicLowercaseTests")
    void testCyrillicLowercase(Cypher cypher, String message, String key) {
        Assertions.assertDoesNotThrow(() -> {
            String encoded = cypher.encode(message, key);
            String decoded = cypher.decode(encoded, key);
            Assertions.assertEquals(message, decoded);
        });
    }

    @Test
    void testPermutationBasicEncoding() {
        String message = "привет";
        String key = "ключ";

        String encoded = Cypher.PERMUTATION.encode(message, key);
        String decoded = Cypher.PERMUTATION.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
        Assertions.assertNotEquals(message, encoded);
    }

    @Test
    void testPermutationNoLeadingTrailingSpaces() {
        String key = "ключ";

        String validMessage = "привет мир";
        Assertions.assertDoesNotThrow(() -> {
            String encoded = Cypher.PERMUTATION.encode(validMessage, key);
            Cypher.PERMUTATION.decode(encoded, key);
        });
    }

    @Test
    void testPermutationWithInternalSpaces() {
        String message = "привет мир";
        String key = "ключ";

        String encoded = Cypher.PERMUTATION.encode(message, key);
        String decoded = Cypher.PERMUTATION.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @Test
    void testColumnPermutationBasicEncoding() {
        String message = "секретное сообщение";
        String key = "ключ";

        String encoded = Cypher.COLUMN_PERMUTATION.encode(message, key);
        String decoded = Cypher.COLUMN_PERMUTATION.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
        Assertions.assertNotEquals(message, encoded);
    }

    @Test
    void testColumnPermutationNoLeadingTrailingSpaces() {
        String key = "шифр";

        String validMessage = "текст без пробелов по краям";
        Assertions.assertDoesNotThrow(() -> {
            String encoded = Cypher.COLUMN_PERMUTATION.encode(validMessage, key);
            Cypher.COLUMN_PERMUTATION.decode(encoded, key);
        });
    }

    @Test
    void testColumnPermutationLongMessage() {
        String message = "это очень длинное секретное сообщение для проверки работы шифра";
        String key = "длинныйключ";

        String encoded = Cypher.COLUMN_PERMUTATION.encode(message, key);
        String decoded = Cypher.COLUMN_PERMUTATION.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @Test
    void testGammaBasicEncoding() {
        String message = "сообщение";
        String key = "гамма";

        String encoded = Cypher.GAMMA.encode(message, key);
        String decoded = Cypher.GAMMA.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @Test
    void testGammaWithSpaces() {
        String message = " текст с пробелами ";
        String key = "ключ";

        String encoded = Cypher.GAMMA.encode(message, key);
        String decoded = Cypher.GAMMA.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @Test
    void testGammaShortKeyLongMessage() {
        String message = "очень длинное сообщение для шифрования";
        String key = "ключ";

        String encoded = Cypher.GAMMA.encode(message, key);
        String decoded = Cypher.GAMMA.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @Test
    void testVigenereBasicEncoding() {
        String message = "атака";
        String key = "ключ";

        String encoded = Cypher.VIGENERE.encode(message, key);
        String decoded = Cypher.VIGENERE.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
        Assertions.assertNotEquals(message, encoded);
    }

    @Test
    void testVigenereNoSpacesInMessage() {
        String key = "ключ";

        String validMessage = "секретноесообщение";
        Assertions.assertDoesNotThrow(() -> {
            String encoded = Cypher.VIGENERE.encode(validMessage, key);
            Cypher.VIGENERE.decode(encoded, key);
        });
    }

    @Test
    void testVigenereNoSpacesInKey() {
        String message = "сообщение";

        String validKey = "секретныйключ";
        Assertions.assertDoesNotThrow(() -> {
            String encoded = Cypher.VIGENERE.encode(message, validKey);
            Cypher.VIGENERE.decode(encoded, validKey);
        });
    }

    @Test
    void testVigenereDifferentKeyLengths() {
        String message = "длинноесообщение";

        String shortKey = "ключ";
        String longKey = "оченьдлинныйключдляшифрования";

        String encoded1 = Cypher.VIGENERE.encode(message, shortKey);
        String decoded1 = Cypher.VIGENERE.decode(encoded1, shortKey);
        Assertions.assertEquals(message, decoded1);

        String encoded2 = Cypher.VIGENERE.encode(message, longKey);
        String decoded2 = Cypher.VIGENERE.decode(encoded2, longKey);
        Assertions.assertEquals(message, decoded2);
    }

    private static Stream<Arguments> provideValidMessagesForAllCyphers() {
        return Stream.of(
                // PERMUTATION
                Arguments.of(Cypher.PERMUTATION, "привет", "ключ"),
                Arguments.of(Cypher.PERMUTATION, "тестовое сообщение", "шифр"),
                Arguments.of(Cypher.PERMUTATION, "абвгдежзийклмнопрстуфхцчшщъыьэюя", "алфавит"),

                // COLUMN_PERMUTATION
                Arguments.of(Cypher.COLUMN_PERMUTATION, "секрет", "код"),
                Arguments.of(Cypher.COLUMN_PERMUTATION, "важное сообщение", "пароль"),
                Arguments.of(Cypher.COLUMN_PERMUTATION, "текст для шифрования", "секретныйключ"),

                // GAMMA
                Arguments.of(Cypher.GAMMA, " пробелы ", "ключ"),
                Arguments.of(Cypher.GAMMA, "текст с пробелами", "гамма"),
                Arguments.of(Cypher.GAMMA, "   много   пробелов   ", "код"),

                // VIGENERE
                Arguments.of(Cypher.VIGENERE, "безпробелов", "ключ"),
                Arguments.of(Cypher.VIGENERE, "шифрвиженера", "секрет"),
                Arguments.of(Cypher.VIGENERE, "длинныйтекст", "короткийключ")
        );
    }

    private static Stream<Arguments> provideCyrillicLowercaseTests() {
        return Stream.of(
                Arguments.of(Cypher.PERMUTATION, "абвгдеёжзийклмнопрстуфхцчшщъыьэюя", "ключ"),
                Arguments.of(Cypher.COLUMN_PERMUTATION, "йцукенгшщзхъфывапролджэячсмитьбю", "шифр"),
                Arguments.of(Cypher.GAMMA, "абвгдеёжзийклмнопрстуфхцчшщъыьэюя", "ключ"),
                Arguments.of(Cypher.VIGENERE, "абвгдеёжзийклмнопрстуфхцчшщъыьэюя", "ключ")
        );
    }

    @ParameterizedTest
    @MethodSource("provideSingleCharacterTests")
    void testRepeatedCharacter(Cypher cypher, String message, String key) {
        String encoded = cypher.encode(message, key);
        String decoded = cypher.decode(encoded, key);
        Assertions.assertEquals(message, decoded);
    }

    private static Stream<Arguments> provideSingleCharacterTests() {
        return Stream.of(
                Arguments.of(Cypher.PERMUTATION, "ааааа", "ключ"),
                Arguments.of(Cypher.COLUMN_PERMUTATION, "ббббб", "шифр"),
                Arguments.of(Cypher.GAMMA, "ввввв", "код"),
                Arguments.of(Cypher.VIGENERE, "ггггг", "пароль")
        );
    }

    @Test
    void testVigenereKeyLongerThanMessage() {
        String message = "мир";
        String key = "оченьдлинныйключкоторыйдлиннеесообщения";

        String encoded = Cypher.VIGENERE.encode(message, key);
        String decoded = Cypher.VIGENERE.decode(encoded, key);

        Assertions.assertEquals(message, decoded);
    }

    @ParameterizedTest
    @EnumSource(Cypher.class)
    void testEncryptionConsistency(Cypher cypher) {
        String message = cypher == Cypher.VIGENERE ? "сообщение" : "со об ще ние";
        String key = "ключ";

        String encoded1 = cypher.encode(message, key);
        String encoded2 = cypher.encode(message, key);

        Assertions.assertEquals(encoded1, encoded2);
    }

}
