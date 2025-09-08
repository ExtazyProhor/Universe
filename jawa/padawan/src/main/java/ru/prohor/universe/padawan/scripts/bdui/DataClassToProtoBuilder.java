package ru.prohor.universe.padawan.scripts.bdui;

import java.util.Arrays;
import java.util.stream.Stream;

public class DataClassToProtoBuilder {
    private static final String section = "";

    private static final String setters = """
            
            
            
            """;

    public static void main(String[] args) {
        all();
    }

    private static void all() {
        from2to3(from1to2()).forEach(System.out::println);
    }

    private static void toProtoBuilder() {
        from1to2().forEach(System.out::println);
    }

    private static void toNotNullProtoBuilder() {
        from2to3(parse()).forEach(System.out::println);
    }

    private static Stream<String> from2to3(Stream<String> stream) {
        return stream.map(s -> {
            String setter = s.substring(1, s.indexOf('('));
            String value = s.substring(s.indexOf('(') + 1);
            return ".setIfNotNull(" + section + ".Builder::" + setter + ", " + value;
        });

    }

    private static Stream<String> from1to2() {
        return parse().map(s -> {
            String setter = Character.toUpperCase(s.charAt(0)) + s.substring(1, s.indexOf('='));
            String value = s.substring(s.indexOf('=') + 1, s.length() - (s.endsWith(",") ? 1 : 0));
            return ".set" + setter + "(" + value + ")";
        });
    }

    private static Stream<String> parse() {
        return Arrays.stream(setters.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty());
    }
}
