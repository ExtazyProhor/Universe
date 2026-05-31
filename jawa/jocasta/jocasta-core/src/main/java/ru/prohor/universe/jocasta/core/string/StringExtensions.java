package ru.prohor.universe.jocasta.core.string;

import java.util.Arrays;
import java.util.List;

public class StringExtensions {
    public static List<String> splitBySpaceChars(String text) {
        return Arrays.stream(text.trim().split("\\s+"))
                .filter(part -> !part.isEmpty())
                .toList();
    }

    public static String sanitizeString(String text) {
        return text.replaceAll("[^\\p{L}\\p{N}]+", "").toLowerCase();
    }

    public static String escape(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\\' -> builder.append("\\\\");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                case '\f' -> builder.append("\\f");
                case '\b' -> builder.append("\\b");
                default -> builder.append(ch);
            }
        }
        return builder.toString();
    }

    public static String fillToLength(String str, char ch, int target) {
        if (str.length() >= target)
            return str;
        return String.valueOf(ch).repeat(target - str.length()) + str;
    }

    public static int utf8Length(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                i++; // skip low surrogate
            } else {
                count += 3;
            }
        }
        return count;
    }
}
