package ru.prohor.universe.kryonet.entities;

import java.util.HashMap;
import java.util.Map;

public class ServerState extends Message {
    public int id;
    public Map<Integer, PlayerState> players = new HashMap<>();

    public ServerState(int id) {
        this.id = id;
    }

    public ServerState(ServerState copy) {
        this.id = copy.id;
        for (Map.Entry<Integer, PlayerState> entry : copy.players.entrySet()) {
            this.players.put(entry.getKey(), new PlayerState(entry.getValue()));
        }
    }
}
