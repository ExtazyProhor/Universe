package ru.prohor.universe.scarif.jwt;

public class JwtVerificationException extends Exception {
    private final boolean warning;

    public JwtVerificationException(Throwable cause) {
        super(cause);
        this.warning = true;
    }

    public JwtVerificationException(String message) {
        super(message);
        this.warning = false;
    }

    public boolean isWarning() {
        return warning;
    }
}
