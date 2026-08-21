package ru.prohor.universe.bobafett.feature.currency.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class DisableHintAboutChangingSelectedCurrencyCallback implements CallbackHandler {
    private final BobaFettUserService bobaFettUserService;

    public DisableHintAboutChangingSelectedCurrencyCallback(BobaFettUserService bobaFettUserService) {
        this.bobaFettUserService = bobaFettUserService;
    }

    @Override
    public String callback() {
        return Callbacks.DISABLE_HINT_ABOUT_CHANGING_SELECTED_CURRENCY;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        bobaFettUserService.safeUpdate(
                message.getChatId(),
                user -> {
                    CurrencySubscriptionOptions options = user.currencySubscriptionOptions().toBuilder()
                            .hintAboutChangingSelectedCurrencyDisabled(true)
                            .build();
                    return user.toBuilder().currencySubscriptionOptions(options).build();
                }
        );
        feedbackExecutor.deleteMessage(message.getChatId(), message.getMessageId());
    }
}
