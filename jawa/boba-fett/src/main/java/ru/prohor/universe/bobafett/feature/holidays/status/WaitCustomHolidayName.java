package ru.prohor.universe.bobafett.feature.holidays.status;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.feature.holidays.CustomHolidaysCreator;
import ru.prohor.universe.bobafett.status.Statuses;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.StatusFlow;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;

import java.time.LocalDate;

@Service
public class WaitCustomHolidayName implements ValuedStatusHandler<String, String> {
    private final CustomHolidaysCreator customHolidaysCreator;

    public WaitCustomHolidayName(CustomHolidaysCreator customHolidaysCreator) {
        this.customHolidaysCreator = customHolidaysCreator;
    }

    @Override
    public String key() {
        return Statuses.WAIT_CUSTOM_HOLIDAY_NAME;
    }

    @Override
    public StatusFlow handle(String value, Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return StatusFlow.CONTINUE;
        }
        long chatId = update.getMessage().getChatId();
        String customHolidayName = update.getMessage().getText();
        if (customHolidayName.startsWith("/"))
            return StatusFlow.CONTINUE;
        if (customHolidayName.equals(Commands.CANCEL)) {
            feedbackExecutor.sendMessage(chatId, "Создание собственного праздника отменено");
            return StatusFlow.EXIT;
        }

        LocalDate date = LocalDate.parse(value);
        feedbackExecutor.sendMessage(
                chatId,
                customHolidaysCreator.addNewCustomHolidayIfNotExists(
                        chatId,
                        date,
                        customHolidayName
                )
        );
        return StatusFlow.EXIT;
    }
}
