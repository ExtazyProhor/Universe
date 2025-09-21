package ru.prohor.universe.jocasta.tgbots.support.status;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface StatusSupport {
    /**
     * @param update telegram api update
     * @return a flag indicating whether to continue update processing
     */
    boolean handleUpdate(Update update);

    static StatusSupport getUnsupported() {
        return new StatusUnsupported();
    }
}
