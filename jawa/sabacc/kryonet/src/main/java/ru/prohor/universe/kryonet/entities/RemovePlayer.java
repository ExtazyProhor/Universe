package ru.prohor.universe.kryonet.entities;

public class RemovePlayer extends Message {
    private final String text;

    public RemovePlayer(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
