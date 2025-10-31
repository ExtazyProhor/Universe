package ru.prohor.universe.kryonet.entities;

public class AddPlayer extends Message {
    private final int playerId;
    private final String playerName;

    public AddPlayer(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }
}
