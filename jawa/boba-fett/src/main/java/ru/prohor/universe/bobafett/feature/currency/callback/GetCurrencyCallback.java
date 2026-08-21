package ru.prohor.universe.bobafett.feature.currency.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.feature.currency.CurrencyMessageGenerator;
import ru.prohor.universe.bobafett.feature.currency.HintAboutChangingSelectedCurrencyService;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class GetCurrencyCallback implements CallbackHandler {
    private final HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService;
    private final BobaFettUserService bobaFettUserService;
    private final CurrencyMessageGenerator currencyMessageGenerator;

    public GetCurrencyCallback(
            HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService,
            BobaFettUserService bobaFettUserService,
            CurrencyMessageGenerator currencyMessageGenerator
    ) {
        this.hintAboutChangingSelectedCurrencyService = hintAboutChangingSelectedCurrencyService;
        this.bobaFettUserService = bobaFettUserService;
        this.currencyMessageGenerator = currencyMessageGenerator;
    }

    @Override
    public String callback() {
        return Callbacks.GET_CURRENCY;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        Long chatId = message.getChatId();
        BobaFettUser user = bobaFettUserService.ensureFindByChatId(chatId);
        feedbackExecutor.editMessageText(
                chatId,
                message.getMessageId(),
                currencyMessageGenerator.getCurrencyMessageFor(user)
        );
        hintAboutChangingSelectedCurrencyService.executeHint(user, feedbackExecutor);
    }
}
