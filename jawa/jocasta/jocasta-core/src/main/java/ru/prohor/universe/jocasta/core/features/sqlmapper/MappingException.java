package ru.prohor.universe.jocasta.core.features.sqlmapper;

/**
 * RuntimeException occurred while mapping an SQL query
 */
public class MappingException extends RuntimeException {
    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
