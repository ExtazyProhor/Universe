package ru.prohor.universe.kryonet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ru.prohor.universe.kryonet.entities.Message;

public class ServerListener extends Listener {
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Message)
            Kryonet.handlers.get(object.getClass()).handleRequest(connection, (Message) object);
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Connected " + connection.getID() + " connection");
        Kryonet.connections.put(connection.getID(), connection);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected " + connection.getID() + " connection");
        Kryonet.connections.remove(connection.getID());
    }
}
