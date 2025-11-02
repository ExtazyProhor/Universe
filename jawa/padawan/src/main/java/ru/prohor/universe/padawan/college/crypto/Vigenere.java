package ru.prohor.universe.padawan.college.crypto;


public class Vigenere {
    public static String encode(String message, String key) {
        message = message.toLowerCase().replace(" ", "");
        key = key.toLowerCase().replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); ++i) {
            int mesCode = CypherUtils.getCharCode(message.charAt(i));
            int keyCode = CypherUtils.getCharCode(key.charAt(i % key.length()));
            result.append(CypherUtils.getChar((mesCode + keyCode) % 33));
        }
        return result.toString();
    }

    public static String decode(String encodedMessage, String key) {
        encodedMessage = encodedMessage.toLowerCase().replace(" ", "");
        key = key.toLowerCase().replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encodedMessage.length(); ++i) {
            int mesCode = CypherUtils.getCharCode(encodedMessage.charAt(i));
            int keyCode = CypherUtils.getCharCode(key.charAt(i % key.length()));
            result.append(CypherUtils.getChar((mesCode + 33 - keyCode) % 33));
        }
        return result.toString();
    }
}
