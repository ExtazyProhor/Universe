package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.MongoStatusStorage;
import ru.prohor.universe.bobafett.feature.holidays.date.ChangeDateKeyboardUtils;
import ru.prohor.universe.bobafett.feature.holidays.date.Payload;
import ru.prohor.universe.bobafett.status.Statuses;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

@Service
public class ChooseCustomHolidayDateCallback extends JsonCallbackHandler<Payload> {
    private static final String NAME_REQUEST = "Теперь напишите название вашего праздника (не более 50 символов)";
    private static final String HOLIDAY_CREATION_CANCELED = "Создание праздника отменено";
    private static final String CANCEL_HOLIDAY_CREATION = "Отменить создание праздника";
    private static final String CHOOSE_HOLIDAY_DATE = "Укажите дату вашего праздника";
    private static final int LEAP_YEAR = 2024;


    private final MongoStatusStorage mongoStatusStorage;

    public ChooseCustomHolidayDateCallback(
            ObjectMapper objectMapper,
            MongoStatusStorage mongoStatusStorage
    ) {
        super(Callbacks.CHOOSE_CUSTOM_HOLIDAY_DATE, Payload.class, objectMapper);
        this.mongoStatusStorage = mongoStatusStorage;
    }

    @Override
    protected boolean handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();

        switch (payload.option()) {
            case APPLY -> {
                mongoStatusStorage.setStatus(
                        chatId,
                        new ValuedStatusStorage.ValuedStatus<>(
                                Statuses.WAIT_CUSTOM_HOLIDAY_NAME,
                                Opt.of(payload.date().toString())
                        )
                );
                feedbackExecutor.editMessageText(chatId, messageId, NAME_REQUEST);
            }
            case CHANGE_DATE -> {
                LocalDate date = ChangeDateKeyboardUtils.calculateChangedDate(payload).withYear(LEAP_YEAR);
                feedbackExecutor.editMessageText(
                        chatId,
                        messageId,
                        CHOOSE_HOLIDAY_DATE,
                        keyboard(date)
                );
            }
            // TODO check null-keyboard
            case CANCEL -> feedbackExecutor.editMessageText(chatId, messageId, HOLIDAY_CREATION_CANCELED/*, null*/);
        }
        return false;
    }

    public EditMessageText createEditMessage(LocalDate date, long chatId, int messageId) {
        InlineKeyboardMarkup keyboard = keyboard(date);
        return EditMessageText.builder()
                .text(CHOOSE_HOLIDAY_DATE)
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(keyboard)
                .build();
    }

    private InlineKeyboardMarkup keyboard(LocalDate date) {
        return ChangeDateKeyboardUtils.makeKeyboardForDate(
                date,
                CANCEL_HOLIDAY_CREATION,
                this::makeCallback,
                false
        );
    }
}
