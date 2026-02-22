package ru.prohor.universe.bobafett.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.api.status.UnknownStatusKeyHandler;

@Service
public class UnknownStatusHandler implements UnknownStatusKeyHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(UnknownStatusHandler.class);

    private final ObjectsEncoder objectsEncoder;

    public UnknownStatusHandler(ObjectsEncoder objectsEncoder) {
        this.objectsEncoder = objectsEncoder;
    }

    @Override
    public StatusFlow handleUnknownActionKey(Update update, String statusKey, FeedbackExecutor feedbackExecutor) {
        log.error("unknown status '{}'. Full update - base64('{}')", statusKey, objectsEncoder.encode(update));
        return StatusFlow.CONTINUE;
    }
}
