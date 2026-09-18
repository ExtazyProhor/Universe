package ru.prohor.universe.scarif.oauth.exception;

public final class OAuthServerErrorException extends OAuthException {
    public OAuthServerErrorException(String message) {
        super(message);
    }

    public OAuthServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
