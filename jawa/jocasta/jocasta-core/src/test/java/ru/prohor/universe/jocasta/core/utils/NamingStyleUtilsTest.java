package ru.prohor.universe.jocasta.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.prohor.universe.jocasta.core.collections.common.CartesianProduct;

import java.util.List;
import java.util.stream.Stream;

public class NamingStyleUtilsTest {
    private static final List<List<String>> TEST_ENTRIES = List.of(
            List.of(
                    "TEST",
                    "test",
                    "Test",
                    "Test",
                    "test",
                    "test",
                    "Test",
                    "test",
                    "TEST"
            ),
            List.of(
                    "TEST STRING",
                    "test string",
                    "Test String",
                    "Test string",
                    "test_string",
                    "testString",
                    "TestString",
                    "test-string",
                    "TEST_STRING"
            ),
            List.of(
                    "TEST STRING MANY WORDS NAMING STYLE TEST",
                    "test string many words naming style test",
                    "Test String Many Words Naming Style Test",
                    "Test string many words naming style test",
                    "test_string_many_words_naming_style_test",
                    "testStringManyWordsNamingStyleTest",
                    "TestStringManyWordsNamingStyleTest",
                    "test-string-many-words-naming-style-test",
                    "TEST_STRING_MANY_WORDS_NAMING_STYLE_TEST"
            ),
            List.of(
                    "A",
                    "a",
                    "A",
                    "A",
                    "a",
                    "a",
                    "A",
                    "a",
                    "A"
            ),
            List.of(
                    "AA",
                    "aa",
                    "Aa",
                    "Aa",
                    "aa",
                    "aa",
                    "Aa",
                    "aa",
                    "AA"
            ),
            List.of(
                    "A B",
                    "a b",
                    "A B",
                    "A b",
                    "a_b",
                    "aB",
                    "AB",
                    "a-b",
                    "A_B"
            ),
            List.of(
                    "A B C",
                    "a b c",
                    "A B C",
                    "A b c",
                    "a_b_c",
                    "aBC",
                    "ABC",
                    "a-b-c",
                    "A_B_C"
            ),
            List.of(
                    "AA B",
                    "aa b",
                    "Aa B",
                    "Aa b",
                    "aa_b",
                    "aaB",
                    "AaB",
                    "aa-b",
                    "AA_B"
            ),
            List.of(
                    "A BB",
                    "a bb",
                    "A Bb",
                    "A bb",
                    "a_bb",
                    "aBb",
                    "ABb",
                    "a-bb",
                    "A_BB"
            ),
            List.of(
                    "AA B CC",
                    "aa b cc",
                    "Aa B Cc",
                    "Aa b cc",
                    "aa_b_cc",
                    "aaBCc",
                    "AaBCc",
                    "aa-b-cc",
                    "AA_B_CC"
            ),
            List.of(
                    "A BB C",
                    "a bb c",
                    "A Bb C",
                    "A bb c",
                    "a_bb_c",
                    "aBbC",
                    "ABbC",
                    "a-bb-c",
                    "A_BB_C"
            )
    );

    private final NamingStyleUtils.NamingStyle[] styles = NamingStyleUtils.NamingStyle.values();

    @Test
    public void testChangeNamingStyles() {
        Stream<Executable> stream = TEST_ENTRIES.stream().flatMap(
                testEntry -> CartesianProduct.ofTwoRanges(styles.length).mapToStream(
                        (i, j) -> createExecutable(testEntry, i, j)
                )
        );
        Assertions.assertAll(stream);
    }

    private Executable createExecutable(List<String> testEntry, int i, int j) {
        String from = testEntry.get(i);
        String should = testEntry.get(j);
        String became = NamingStyleUtils.changeStyle(
                styles[i],
                styles[j],
                from
        );
        String message = "string '%s' became '%s' (should be '%s')"
                .formatted(from, became, should);

        return () -> Assertions.assertEquals(
                should,
                became,
                message
        );
    }
}
