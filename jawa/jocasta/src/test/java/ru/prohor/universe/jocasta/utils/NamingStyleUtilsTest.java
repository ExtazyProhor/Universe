package ru.prohor.universe.jocasta.utils;

import java.util.List;

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

    private static void test() {
        NamingStyleUtils.NamingStyle[] styles = NamingStyleUtils.NamingStyle.values();

        for (List<String> testEntry : TEST_ENTRIES) {
            for (int i = 0; i < styles.length; ++i) {
                for (int j = 0; j < styles.length; ++j) {
                    if (i == j)
                        continue;
                    String from = testEntry.get(i);
                    String should = testEntry.get(j);
                    String became = NamingStyleUtils.changeStyle(styles[i], styles[j], from);

                    if (!became.equals(should))
                        System.out.printf("'%s' -> '%s' ('%s')", from, became, should);
                }
            }
        }
    }
}
