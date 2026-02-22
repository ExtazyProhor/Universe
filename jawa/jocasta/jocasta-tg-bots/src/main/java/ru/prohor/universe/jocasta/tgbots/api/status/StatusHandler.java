package ru.prohor.universe.jocasta.tgbots.api.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface StatusHandler<K> extends ActionHandler<K> {
    /**
     * @param update           telegram api update
     * @param feedbackExecutor interface for sending feedback to users
     * @return a {@link StatusFlow status flow} flag indicating whether to continue update processing
     */
    StatusFlow handle(Update update, FeedbackExecutor feedbackExecutor);
}
