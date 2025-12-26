package ru.prohor.universe.bobafett.feature.holidays.status;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.feature.holidays.CustomHolidaysService;
import ru.prohor.universe.bobafett.status.Statuses;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;

@Service
// TODO json Valued Status Handler
public class WaitCustomHolidayName implements ValuedStatusHandler<String, String> {
    private final CustomHolidaysService customHolidaysService;

    public WaitCustomHolidayName(CustomHolidaysService customHolidaysService) {
        this.customHolidaysService = customHolidaysService;
    }

    @Override
    public String key() {
        return Statuses.WAIT_CUSTOM_HOLIDAY_NAME;
    }

    @Override
    public boolean handle(String value, Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }
        long chatId = update.getMessage().getChatId();
        String customHolidayName = update.getMessage().getText();
        if (customHolidayName.startsWith("/"))
            return true;
        if (customHolidayName.equals(Commands.CANCEL)) {
            feedbackExecutor.sendMessage(chatId, "Создание собственного праздника отменено");
            return false;
        }

        LocalDate date = LocalDate.parse(value);
        feedbackExecutor.sendMessage(
                chatId,
                customHolidaysService.addNewCustomHolidayIfNotExists(
                        chatId,
                        date.getDayOfMonth(),
                        date.getMonthOfYear(),
                        customHolidayName
                )
        );
        return false;
    }
}
