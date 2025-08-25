package ru.prohor.universe.scarif.services.refresh;

public class UserTokenParsingException extends Exception {
    UserTokenParsingException(String parameterName, String parameter) {
        super("illegal " + parameterName + " format: {" + maskToken(parameter) + "}");
    }

    private static String maskToken(String token) {
        return token.length() < 10 ? token : token.substring(0, token.length() - 5) + "*****";
    }
}
