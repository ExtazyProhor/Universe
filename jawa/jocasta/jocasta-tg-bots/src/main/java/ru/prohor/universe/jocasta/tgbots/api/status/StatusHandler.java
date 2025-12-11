package ru.prohor.universe.jocasta.tgbots.api.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.ActionHandler;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface StatusHandler<K, V> extends ActionHandler<K> {
    /**
     * @param statusValue      value of status
     * @param update           telegram api update
     * @param feedbackExecutor interface for sending feedback to users
     * @return a flag indicating whether to continue update processing
     */
    boolean handle(Opt<V> statusValue, Update update, FeedbackExecutor feedbackExecutor);

    default V ensureValue(Opt<V> statusValue) {
        return statusValue.orElseThrow(
                () -> new RuntimeException("Status for key " + key() + " should have a value, but it doesn't")
        );
    }
}
