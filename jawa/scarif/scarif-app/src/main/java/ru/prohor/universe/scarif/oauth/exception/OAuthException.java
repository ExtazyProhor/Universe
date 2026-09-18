package ru.prohor.universe.scarif.oauth.exception;

public sealed abstract class OAuthException
        extends Exception
        permits OAuthServerErrorException, OAuthClientErrorException {
    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
