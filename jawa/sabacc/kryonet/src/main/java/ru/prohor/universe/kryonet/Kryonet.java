package ru.prohor.universe.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import ru.prohor.universe.kryonet.entities.AddPlayer;
import ru.prohor.universe.kryonet.entities.Bullet;
import ru.prohor.universe.kryonet.entities.PlayerState;
import ru.prohor.universe.kryonet.entities.RegisterError;
import ru.prohor.universe.kryonet.entities.RegisterRequest;
import ru.prohor.universe.kryonet.entities.RegisterSuccess;
import ru.prohor.universe.kryonet.entities.RemovePlayer;
import ru.prohor.universe.kryonet.entities.ServerState;
import ru.prohor.universe.kryonet.handlers.GameHandler;
import ru.prohor.universe.kryonet.handlers.RegisterHandler;
import ru.prohor.universe.kryonet.handlers.RemoveHandler;
import ru.prohor.universe.kryonet.handlers.RequestHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Kryonet {
    public static Server server;
    public static ServerHandler serverHandler;

    public static Map<Class<?>, RequestHandler> handlers;
    public static Map<Integer, Connection> connections;
    public static Map<Integer, String> usersIdAndNames;

    public static void main(String[] args) throws IOException {
        handlers = new HashMap<>();
        handlers.put(RegisterRequest.class, new RegisterHandler());
        handlers.put(PlayerState.class, new GameHandler());
        handlers.put(RemovePlayer.class, new RemoveHandler());

        connections = new HashMap<>();
        usersIdAndNames = new HashMap<>();

        server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(AddPlayer.class);
        kryo.register(Bullet.class);
        kryo.register(PlayerState.class);
        kryo.register(RegisterError.class);
        kryo.register(RegisterRequest.class);
        kryo.register(RegisterSuccess.class);
        kryo.register(RemovePlayer.class);
        kryo.register(ServerState.class);
        server.start();
        server.bind(54555, 54777);
        server.addListener(new ServerListener());

        serverHandler = new ServerHandler();
        serverHandler.start();
    }
}
