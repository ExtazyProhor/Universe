package ru.prohor.universe.jocasta.tgbots.support;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;

public class StatusUnsupported implements StatusSupport {
    @Override
    public StatusFlow handle(Update payload, FeedbackExecutor feedbackExecutor) {
        return StatusFlow.CONTINUE;
    }
}
