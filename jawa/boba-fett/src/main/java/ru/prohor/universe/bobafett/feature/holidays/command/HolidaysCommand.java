package ru.prohor.universe.bobafett.feature.holidays.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.feature.holidays.callback.CustomHolidayInitCallback;
import ru.prohor.universe.bobafett.feature.holidays.callback.GetHolidaysInitCallback;
import ru.prohor.universe.bobafett.feature.holidays.callback.ImportHolidaysInitCallback;
import ru.prohor.universe.bobafett.feature.holidays.callback.SubscribeHolidaysInitCallback;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.List;

@Service
public class HolidaysCommand implements CommandHandler {
    private static final String START_MESSAGE = "Выберите действие";
    private final InlineKeyboardMarkup keyboard;

    public HolidaysCommand(
            GetHolidaysInitCallback getHolidaysInitCallback,
            CustomHolidayInitCallback customHolidayInitCallback,
            ImportHolidaysInitCallback importHolidaysInitCallback,
            SubscribeHolidaysInitCallback subscribeHolidaysInitCallback
    ) {
        keyboard = InlineKeyboardUtils.getColumnInlineKeyboard(
                List.of(
                        "узнать праздники в определенный день",
                        "управление собственными праздниками",
                        "импортировать праздники из другого чата",
                        "ежедневная рассылка праздников"
                ),
                List.of(
                        getHolidaysInitCallback.key(),
                        customHolidayInitCallback.key(),
                        importHolidaysInitCallback.key(),
                        subscribeHolidaysInitCallback.key()
                )
        );
    }

    @Override
    public String command() {
        return "/holidays";
    }

    @Override
    public Opt<String> description() {
        return Opt.of("праздники");
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
