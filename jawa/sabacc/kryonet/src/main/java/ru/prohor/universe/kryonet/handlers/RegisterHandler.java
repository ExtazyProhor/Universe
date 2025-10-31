package ru.prohor.universe.kryonet.handlers;

import com.esotericsoftware.kryonet.Connection;
import ru.prohor.universe.kryonet.Kryonet;
import ru.prohor.universe.kryonet.entities.Message;
import ru.prohor.universe.kryonet.entities.RegisterError;
import ru.prohor.universe.kryonet.entities.RegisterRequest;
import ru.prohor.universe.kryonet.entities.RegisterSuccess;

public class RegisterHandler implements RequestHandler {
    @Override
    public void handleRequest(Connection connection, Message message) {
        RegisterRequest request = (RegisterRequest) message;
        String name = request.getName();
        int id = connection.getID();
        Message responseMessage;
        if (Kryonet.usersIdAndNames.containsValue(name)) {
            responseMessage = new RegisterError("User with name \"" + name + "\" already exists");
        } else {
            Kryonet.usersIdAndNames.put(id, name);
            responseMessage = new RegisterSuccess(id, Kryonet.usersIdAndNames, Kryonet.serverHandler.getCurrentState());
        }
        connection.sendTCP(responseMessage);
    }
}
