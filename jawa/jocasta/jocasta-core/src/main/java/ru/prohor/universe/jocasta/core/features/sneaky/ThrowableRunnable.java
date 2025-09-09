package ru.prohor.universe.jocasta.core.features.sneaky;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}
