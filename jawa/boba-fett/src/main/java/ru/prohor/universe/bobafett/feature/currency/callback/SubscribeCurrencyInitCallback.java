package ru.prohor.universe.bobafett.feature.currency.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class SubscribeCurrencyInitCallback implements CallbackHandler {
    private final SubscribeCurrencyCallback subscribeCurrencyCallback;

    public SubscribeCurrencyInitCallback(SubscribeCurrencyCallback subscribeCurrencyCallback) {
        this.subscribeCurrencyCallback = subscribeCurrencyCallback;
    }

    @Override
    public String callback() {
        return Callbacks.SUBSCRIBE_CURRENCY_INIT;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        subscribeCurrencyCallback.sendMenu(chatId, messageId, feedbackExecutor);
    }
}
