package ru.prohor.universe.jocasta.tgbots.support.callback;

public interface CallbackSupport {
    static CallbackSupport getUnsupported() {
        return new CallbackUnsupported();
    }
}
