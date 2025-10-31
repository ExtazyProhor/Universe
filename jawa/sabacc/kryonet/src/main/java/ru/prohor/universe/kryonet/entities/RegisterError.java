package ru.prohor.universe.kryonet.entities;

public class RegisterError extends Message {
    private final String text;

    public RegisterError(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
