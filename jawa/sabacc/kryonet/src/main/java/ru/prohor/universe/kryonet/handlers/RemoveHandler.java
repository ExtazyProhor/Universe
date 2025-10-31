package ru.prohor.universe.kryonet.handlers;

import com.esotericsoftware.kryonet.Connection;
import ru.prohor.universe.kryonet.Kryonet;
import ru.prohor.universe.kryonet.entities.Message;
import ru.prohor.universe.kryonet.entities.RemovePlayer;

public class RemoveHandler implements RequestHandler {
    @Override
    public void handleRequest(Connection connection, Message message) {
        RemovePlayer removeRequest = (RemovePlayer) message;
        int id = connection.getID();
        System.out.println(removeRequest.getText());
        Kryonet.serverHandler.getCurrentState().players.remove(id);
        Kryonet.usersIdAndNames.remove(id);
        connection.close();
    }
}
