package ru.prohor.universe.padawan.college.crypto;

public class ColumnPermutation {
    public static String encode(String message, String key) {
        int[] codes = CypherUtils.getCodesFromKey(key);
        message = CypherUtils.extendMessageWithSpaces(message, key.length());
        StringBuilder result = new StringBuilder();
        int rows = message.length() / key.length();
        for (int i = 0; i < rows; ++i)
            for (int code : codes)
                result.append(message.charAt(rows * code + i));
        return result.toString().trim();
    }

    public static String decode(String encodedMessage, String key) {
        int[] codes = CypherUtils.getCodesFromKey(key);
        encodedMessage = CypherUtils.extendMessageWithSpaces(encodedMessage, key.length());
        StringBuilder result = new StringBuilder(" ".repeat(encodedMessage.length()));
        int rows = encodedMessage.length() / key.length();
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < key.length(); ++j)
                result.setCharAt(codes[j] * rows + i, encodedMessage.charAt(i * key.length() + j));
        return result.toString().trim();
    }
}
