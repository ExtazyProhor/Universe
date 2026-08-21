package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.feature.currency.callback.DisableHintAboutChangingSelectedCurrencyCallback;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.List;

@Service
public class HintAboutChangingSelectedCurrencyService {
    private static final String MESSAGE = "Напоминаю, вы можете изменить список валют, курсы которых получаете, " +
            "используя команду " + Commands.CURRENCY + " -> 'изменить список валют'";
    private static final String DISABLE = "Отключить это напоминание";

    private final DisableHintAboutChangingSelectedCurrencyCallback disableHintAboutChangingSelectedCurrencyCallback;

    public HintAboutChangingSelectedCurrencyService(
            DisableHintAboutChangingSelectedCurrencyCallback disableHintAboutChangingSelectedCurrencyCallback
    ) {
        this.disableHintAboutChangingSelectedCurrencyCallback = disableHintAboutChangingSelectedCurrencyCallback;
    }

    public void executeHint(BobaFettUser user, FeedbackExecutor feedbackExecutor) {
        if (user.currencySubscriptionOptions().hintAboutChangingSelectedCurrencyDisabled()) {
            return;
        }
        feedbackExecutor.sendMessage(
                user.chatId(),
                MESSAGE,
                InlineKeyboardUtils.getColumnInlineKeyboard(
                        List.of(DISABLE),
                        List.of(disableHintAboutChangingSelectedCurrencyCallback.callback())
                )
        );
    }
}
