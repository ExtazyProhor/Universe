package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class GetHolidaysInitCallback implements CallbackHandler {
    private static final String START_MESSAGE = "Выберите день, праздники которого хотите узнать";

    private final GetHolidaysCallback getHolidaysCallback;

    public GetHolidaysInitCallback(GetHolidaysCallback getHolidaysCallback) {
        this.getHolidaysCallback = getHolidaysCallback;
    }

    @Override
    public String callback() {
        return "holidays/init-get";
    }

    @Override
    public boolean handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        EditMessageText editMessageText = EditMessageText.builder()
                .text(START_MESSAGE)
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .replyMarkup(getHolidaysCallback.getKeyboard())
                .build();
        feedbackExecutor.editMessageText(editMessageText);
        return false;
    }
}
