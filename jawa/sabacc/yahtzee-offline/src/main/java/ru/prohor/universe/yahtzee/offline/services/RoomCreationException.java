package ru.prohor.universe.yahtzee.offline.services;

public class RoomCreationException extends Exception { // TODO поменять на Result из jocasta-core
    public RoomCreationException(String message) {
        super(message);
    }
}
