package ru.prohor.universe.yahtzee.data;

public class MongoDatabaseException extends RuntimeException {
    public MongoDatabaseException(String message) {
        super(message);
    }
}
