package ru.prohor.universe.kryonet.entities;

public class RegisterRequest extends Message {
    private final String name;

    public RegisterRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
