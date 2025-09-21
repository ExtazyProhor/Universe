package ru.prohor.universe.jocasta.tgbots.support.status;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StatusUnsupported implements StatusSupport {
    @Override
    public boolean handleUpdate(Update update) {
        return true;
    }
}
