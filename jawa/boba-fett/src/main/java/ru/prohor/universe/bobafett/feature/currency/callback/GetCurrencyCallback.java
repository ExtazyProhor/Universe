package ru.prohor.universe.bobafett.feature.currency.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.feature.currency.CurrencyMessageGenerator;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class GetCurrencyCallback implements CallbackHandler {
    private final CurrencyMessageGenerator currencyMessageGenerator;

    public GetCurrencyCallback(CurrencyMessageGenerator currencyMessageGenerator) {
        this.currencyMessageGenerator = currencyMessageGenerator;
    }

    @Override
    public String callback() {
        return Callbacks.GET_CURRENCY;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(
                message.getChatId(),
                message.getMessageId(),
                currencyMessageGenerator.getCurrencyMessage()
        );
    }
}
