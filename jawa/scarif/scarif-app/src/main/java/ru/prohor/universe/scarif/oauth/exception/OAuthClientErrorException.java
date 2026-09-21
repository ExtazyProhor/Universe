package ru.prohor.universe.scarif.oauth.exception;

import lombok.Getter;

@Getter
public final class OAuthClientErrorException extends OAuthException {
    private final String messageForClients;

    public OAuthClientErrorException(String message, String messageForClients) {
        super(message);
        this.messageForClients = messageForClients;
    }
}
