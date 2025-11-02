package ru.prohor.universe.padawan.college.crypto;

public class Gamma {
    private static final int ALPHABET_CHARS = 34;

    public static String encode(String message, String key) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); ++i) {
            int charCode = CypherUtils.getCharCode(message.charAt(i));
            int gammaCode = CypherUtils.getCharCode(key.charAt(i % key.length()));
            builder.append(CypherUtils.getChar((charCode + gammaCode + 1) % ALPHABET_CHARS));
        }
        return builder.toString();
    }

    public static String decode(String encodedMessage, String key) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < encodedMessage.length(); ++i) {
            int charCode = CypherUtils.getCharCode(encodedMessage.charAt(i));
            int gammaCode = CypherUtils.getCharCode(key.charAt(i % key.length()));
            builder.append(CypherUtils.getChar((charCode - gammaCode + ALPHABET_CHARS - 1) % ALPHABET_CHARS));
        }
        return builder.toString();
    }
}
