package ru.prohor.universe.bobafett.feature.currency.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.feature.currency.callback.ChangeSelectedCurrenciesCallback;
import ru.prohor.universe.bobafett.feature.currency.callback.GetCurrencyCallback;
import ru.prohor.universe.bobafett.feature.currency.callback.SubscribeCurrencyInitCallback;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.List;

@Service
public class CurrencyCommand implements CommandHandler {
    private static final String START_MESSAGE = "Выберите действие";
    private final InlineKeyboardMarkup keyboard;

    public CurrencyCommand(
            GetCurrencyCallback getCurrencyCallback,
            SubscribeCurrencyInitCallback subscribeCurrencyInitCallback,
            ChangeSelectedCurrenciesCallback changeSelectedCurrenciesCallback
    ) {
        keyboard = InlineKeyboardUtils.getColumnInlineKeyboard(
                List.of(
                        "узнать текущий курс валют",
                        "ежедневная рассылка курса валют",
                        "изменить список валют"
                ),
                List.of(
                        getCurrencyCallback.key(),
                        subscribeCurrencyInitCallback.key(),
                        changeSelectedCurrenciesCallback.initialCallback()
                )
        );
    }

    @Override
    public String command() {
        return Commands.CURRENCY;
    }

    @Override
    public Opt<String> description() {
        return Opt.of("курс валют");
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        SendMessage sendMessage = SendMessage.builder()
                .text(START_MESSAGE)
                .replyMarkup(keyboard)
                .chatId(message.getChatId())
                .build();
        feedbackExecutor.sendMessage(sendMessage);
    }
}
