package ru.prohor.universe.kryonet.handlers;

import com.esotericsoftware.kryonet.Connection;
import ru.prohor.universe.kryonet.entities.Message;

public interface RequestHandler {
    void handleRequest(Connection connection, Message message);
}
