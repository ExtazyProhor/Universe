package ru.prohor.universe.venator.old;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

public class ProcessLauncherImpl {
    public ServiceInfo launch(String name, Path jarPath) throws IOException {
        Process process = new ProcessBuilder("java", "-jar", jarPath.toString())
                .redirectOutput(ProcessBuilder.Redirect.appendTo(jarPath.resolveSibling(name + ".log").toFile()))
                .redirectErrorStream(true)
                .start();

        return new ServiceInfo(
                name,
                jarPath,
                process.pid(),
                Instant.now(),
                true
        );
    }

    public boolean isAlive(long pid) {
        return ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
    }

    public void kill(long pid) {
        ProcessHandle.of(pid).ifPresent(handle -> {
            if (handle.isAlive())
                handle.destroy();
        });
    }
}
