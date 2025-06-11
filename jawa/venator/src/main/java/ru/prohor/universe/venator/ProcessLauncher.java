package ru.prohor.universe.venator;

import java.io.IOException;
import java.nio.file.Path;

public interface ProcessLauncher {
    ServiceInfo launchService(String name, Path jarPath) throws IOException;

    boolean isAlive(long pid);

    void killService(String name);
}
