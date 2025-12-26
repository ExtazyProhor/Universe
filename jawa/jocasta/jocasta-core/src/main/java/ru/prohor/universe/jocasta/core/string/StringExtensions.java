package ru.prohor.universe.jocasta.core.string;

public class StringExtensions {
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
}
