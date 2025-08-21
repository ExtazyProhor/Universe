package ru.prohor.universe.jocasta.core.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SimpleTemplateEngine {
    private static final Predicate<String> KEYS_PATTERN = Pattern.compile("^[\\w.-]+$").asMatchPredicate();
    private static final String CONDITION_KEY = "??";
    private static final String VALUE_KEY = "$$";
    private static final int KEY_SIZE = 2;

    private final Set<String> retained;
    private final Map<String, String> values;

    public SimpleTemplateEngine(Set<String> retained, Map<String, String> values) {
        this.retained = new HashSet<>(retained);
        this.values = new HashMap<>(values);
    }

    public String process(String s) {
        return replacer(remover(s));
    }

    private String remover(String s) {
        StringBuilder sb = new StringBuilder();
        int opening;
        int closing = -KEY_SIZE;
        while ((opening = s.indexOf(CONDITION_KEY, closing + KEY_SIZE)) != -1) {
            sb.append(s, closing == -KEY_SIZE ? 0 : closing + KEY_SIZE, opening);
            closing = s.indexOf(CONDITION_KEY, opening + KEY_SIZE);
            if (closing == -1)
                throw TemplateParsingException.noClosingSymbols(CONDITION_KEY);
            String key = s.substring(opening + KEY_SIZE, closing);
            testKey(key);
            int start = closing + KEY_SIZE;
            opening = s.indexOf(CONDITION_KEY, closing + KEY_SIZE);
            if (opening == -1)
                throw TemplateParsingException.noClosingConditionKey(key);
            closing = s.indexOf(CONDITION_KEY, opening + KEY_SIZE);
            if (closing == -1)
                throw TemplateParsingException.noClosingSymbols(CONDITION_KEY);
            String key2 = s.substring(opening + KEY_SIZE, closing);
            testKey(key2);
            if (!key.equals(key2))
                throw TemplateParsingException.differentKeys(key, key2);
            if (retained.contains(key))
                sb.append(s, start, opening);
        }
        sb.append(s.substring(closing == -KEY_SIZE ? 0 : closing + KEY_SIZE));
        return sb.toString();
    }

    private void testKey(String key) {
        if (!KEYS_PATTERN.test(key))
            throw TemplateParsingException.illegalChars(key);
    }

    private String replacer(String s) {
        StringBuilder sb = new StringBuilder();
        int opening;
        int closing = -KEY_SIZE;
        while ((opening = s.indexOf(VALUE_KEY, closing + KEY_SIZE)) != -1) {
            sb.append(s, closing == -KEY_SIZE ? 0 : closing + KEY_SIZE, opening);
            closing = s.indexOf(VALUE_KEY, opening + KEY_SIZE);
            if (closing == -1)
                throw TemplateParsingException.noClosingSymbols(VALUE_KEY);
            String key = s.substring(opening + KEY_SIZE, closing);
            testKey(key);

            if (!values.containsKey(key))
                continue;
            sb.append(values.get(key));
        }
        sb.append(s.substring(closing == -KEY_SIZE ? 0 : closing + KEY_SIZE));
        return sb.toString();
    }

    public static class TemplateParsingException extends RuntimeException {
        public static final int NO_CLOSING_SYMBOLS_ERROR_CODE = 1;
        public static final int DIFFERENT_CONDITION_KEYS_ERROR_CODE = 2;
        public static final int ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE = 3;
        public static final int NO_CLOSING_CONDITION_KEY_ERROR_CODE = 4;

        private final int errorCode;

        private TemplateParsingException(String s, int errorCode) {
            super(s);
            this.errorCode = errorCode;
        }

        public int errorCode() {
            return errorCode;
        }

        private static TemplateParsingException noClosingSymbols(String symbols) {
            return new TemplateParsingException(
                    "template does not contains closing %s".formatted(symbols),
                    NO_CLOSING_SYMBOLS_ERROR_CODE
            );
        }

        private static TemplateParsingException differentKeys(String key1, String key2) {
            return new TemplateParsingException(
                    "condition keys are different ({%s} and {%s})".formatted(key1, key2),
                    DIFFERENT_CONDITION_KEYS_ERROR_CODE
            );
        }

        private static TemplateParsingException illegalChars(String key) {
            return new TemplateParsingException(
                    "key {%s} contains illegal characters".formatted(key),
                    ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            );
        }

        private static TemplateParsingException noClosingConditionKey(String key) {
            return new TemplateParsingException(
                    "template does not contains closing condition key for opening key {%s}".formatted(key),
                    NO_CLOSING_CONDITION_KEY_ERROR_CODE
            );
        }
    }
}
