package ru.prohor.universe.jocasta.core.utils;

import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class NamingStyleUtils {
    private NamingStyleUtils() {}

    private static final char SPACE = ' ';
    private static final char UNDERSCORE = '_';
    private static final char DASH = '-';

    private static final MonoFunction<String, List<String>> bySpace = byChar(SPACE);
    private static final MonoFunction<String, List<String>> byUnderscore = byChar(UNDERSCORE);
    private static final MonoFunction<String, List<String>> byDash = byChar(DASH);

    private static MonoFunction<String, List<String>> byChar(char ch) {
        return str -> Arrays.asList(str.split(String.valueOf(ch)));
    }

    private static MonoFunction<List<String>, String> mapAndJoin(MonoFunction<String, String> mapper, char delimiter) {
        return words -> words.stream().map(mapper).collect(Collectors.joining(String.valueOf(delimiter)));
    }

    private static String firstCharUpperWord(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    private static final MonoFunction<String, List<String>> upperCharParser = s -> {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            if (Character.isUpperCase(ch) && !sb.isEmpty()) {
                list.add(sb.toString());
                sb.setLength(0);
            }
            sb.append(ch);
        }
        list.add(sb.toString());
        return list;
    };

    public static String changeStyle(NamingStyle from, NamingStyle to, String str) {
        return from == to ? str : to.fromWords.apply(from.toWords.apply(str));
    }

    public enum NamingStyle {
        UPPER_CASE(
                bySpace,
                mapAndJoin(String::toUpperCase, SPACE),
                "EXAMPLE LINE"
        ),

        LOWER_CASE(
                bySpace,
                mapAndJoin(String::toLowerCase, SPACE),
                "example line"
        ),

        TITLE_CASE(
                bySpace,
                mapAndJoin(NamingStyleUtils::firstCharUpperWord, SPACE),
                "Example Line"
        ),

        SENTENCE_CASE(
                bySpace,
                words -> {
                    StringBuilder sb = new StringBuilder(firstCharUpperWord(words.get(0)));
                    words.stream().skip(1).forEach(s -> sb.append(" ").append(s.toLowerCase()));
                    return sb.toString();
                },
                "Example line"
        ),

        SNAKE_CASE(
                byUnderscore,
                mapAndJoin(String::toLowerCase, UNDERSCORE),
                "example_line"
        ),

        CAMEL_CASE(
                upperCharParser,
                words -> {
                    StringBuilder sb = new StringBuilder(words.get(0).toLowerCase());
                    words.stream().skip(1).forEach(s -> sb.append(firstCharUpperWord(s)));
                    return sb.toString();
                },
                "exampleLine"
        ),

        PASCAL_CASE(
                upperCharParser,
                words -> words.stream().map(NamingStyleUtils::firstCharUpperWord).collect(Collectors.joining()),
                "ExampleLine"
        ),

        KEBAB_CASE(
                byDash,
                mapAndJoin(String::toLowerCase, DASH),
                "example-line"
        ),

        SCREAM_CASE(
                byUnderscore,
                mapAndJoin(String::toUpperCase, UNDERSCORE),
                "EXAMPLE_LINE"
        );

        private final MonoFunction<String, List<String>> toWords;
        private final MonoFunction<List<String>, String> fromWords;
        public final String example;

        NamingStyle(
                MonoFunction<String, List<String>> toWords,
                MonoFunction<List<String>, String> fromWords,
                String example
        ) {
            this.toWords = toWords;
            this.fromWords = fromWords;
            this.example = example;
        }
    }
}
