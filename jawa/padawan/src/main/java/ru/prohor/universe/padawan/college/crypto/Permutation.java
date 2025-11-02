package ru.prohor.universe.padawan.college.crypto;

public class Permutation {
    public static String encode(String message, String key) {
        int[] codes = CypherUtils.getCodesFromKey(key);
        int toAppend = message.length() % key.length();
        message = message.toLowerCase() + " ".repeat((toAppend == 0 ? 0 : (key.length() - toAppend)));
        int blocks = message.length() / key.length();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < blocks; ++i)
            for (int code : codes)
                result.append(message.charAt(i * key.length() + code));
        return result.toString().trim();
    }

    public static String decode(String encodedMessage, String key) {
        encodedMessage = CypherUtils.extendMessageWithSpaces(encodedMessage, key.length());
        int[] codes = CypherUtils.getCodesFromKey(key);
        encodedMessage = encodedMessage.toLowerCase();
        int blocks = encodedMessage.length() / key.length();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < blocks; ++i) {
            StringBuilder block = new StringBuilder(" ".repeat(key.length()));
            for (int j = 0; j < codes.length; ++j)
                block.setCharAt(codes[j], encodedMessage.charAt(i * key.length() + j));
            result.append(block);
        }
        return result.toString().trim();
    }
}
