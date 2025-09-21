package ru.prohor.universe.jocasta.tgbots.support.command;

public interface CommandSupport {
    static CommandSupport getUnsupported() {
        return new CommandUnsupported();
    }
}
