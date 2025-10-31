package ru.prohor.universe.kryonet;

import ru.prohor.universe.kryonet.entities.ServerState;

import java.util.HashMap;
import java.util.Map;

public class ServerHandler extends Thread {
    public boolean SERVER_ACTIVE = true;
    public static final int TICK_RATE = 30;
    public static final float TIME_TO_SAVE_STATES = 2;
    public static final int MAX_STATES_QUANTITY = (int) (TICK_RATE * TIME_TO_SAVE_STATES);
    public static final int MILLS_IN_TICK = 1000 / TICK_RATE;

    private final Map<Integer, ServerState> states = new HashMap<>();
    private final ServerState state = new ServerState(0);

    public void updateState() {
        states.put(state.id, new ServerState(state));
        if (states.size() > MAX_STATES_QUANTITY)
            states.remove(state.id - MAX_STATES_QUANTITY);
        state.id++;
    }

    public ServerState getCurrentState() {
        return state;
    }

    @Override
    public void run() {
        while (SERVER_ACTIVE) {
            long startTime = System.currentTimeMillis();
            // logic
            long time = System.currentTimeMillis() - startTime;
            System.out.println("Render use " + time + " mills");
            if (time > MILLS_IN_TICK)
                continue;
            try {
                Thread.sleep(MILLS_IN_TICK - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                SERVER_ACTIVE = false;
            }
        }
    }
}
