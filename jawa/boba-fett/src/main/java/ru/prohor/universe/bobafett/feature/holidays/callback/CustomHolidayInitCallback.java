package ru.prohor.universe.bobafett.feature.holidays.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class CustomHolidayInitCallback implements CallbackHandler {
    private static final String TEXT =
            "Вы хотите создать новый собственный праздник, удалить существующий или просмотреть их список?";

    private final CustomHolidayCallback customHolidayCallback;

    public CustomHolidayInitCallback(CustomHolidayCallback customHolidayCallback) {
        this.customHolidayCallback = customHolidayCallback;
    }

    @Override
    public String callback() {
        return "holidays/init-custom";
    }

    @Override
    public boolean handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(
                message.getChatId(),
                message.getMessageId(),
                TEXT,
                customHolidayCallback.keyboard
        );
        return false;
    }
}
