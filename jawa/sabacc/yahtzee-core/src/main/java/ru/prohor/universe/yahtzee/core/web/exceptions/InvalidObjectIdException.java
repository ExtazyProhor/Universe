package ru.prohor.universe.yahtzee.core.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid objectId format")
public class InvalidObjectIdException extends RuntimeException {
    public InvalidObjectIdException(String objectId, Exception cause) {
        super("Invalid objectId format: '" + objectId + "'", cause);
    }
}
