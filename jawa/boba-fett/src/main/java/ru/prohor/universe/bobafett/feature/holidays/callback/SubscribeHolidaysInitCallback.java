package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class SubscribeHolidaysInitCallback implements CallbackHandler {
    private final SubscribeHolidaysCallback subscribeHolidaysCallback;

    public SubscribeHolidaysInitCallback(SubscribeHolidaysCallback subscribeHolidaysCallback) {
        this.subscribeHolidaysCallback = subscribeHolidaysCallback;
    }

    @Override
    public String callback() {
        return Callbacks.SUBSCRIBE_HOLIDAYS_INIT;
    }

    @Override
    public boolean handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        subscribeHolidaysCallback.sendMenu(chatId, messageId, feedbackExecutor);
        return false;
    }
}
