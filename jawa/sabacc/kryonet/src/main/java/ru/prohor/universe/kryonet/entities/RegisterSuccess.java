package ru.prohor.universe.kryonet.entities;

import java.util.Map;

public class RegisterSuccess extends Message {
    private final int assignedId;
    private final Map<Integer, String> usersIdAndNames;
    private final ServerState serverState;

    public RegisterSuccess(int assignedId, Map<Integer, String> userIdAndNames, ServerState serverState) {
        this.assignedId = assignedId;
        this.usersIdAndNames = userIdAndNames;
        this.serverState = serverState;
    }

    public int getAssignedId() {
        return assignedId;
    }

    public Map<Integer, String> getUsersIdAndNames() {
        return usersIdAndNames;
    }

    public ServerState getServerState() {
        return serverState;
    }
}
