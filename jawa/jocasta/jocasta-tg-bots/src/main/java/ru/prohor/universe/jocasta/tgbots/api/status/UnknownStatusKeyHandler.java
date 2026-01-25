package ru.prohor.universe.jocasta.tgbots.api.status;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface UnknownStatusKeyHandler<K> {
    StatusFlow handleUnknownActionKey(Update update, K key, FeedbackExecutor feedbackExecutor);
}
