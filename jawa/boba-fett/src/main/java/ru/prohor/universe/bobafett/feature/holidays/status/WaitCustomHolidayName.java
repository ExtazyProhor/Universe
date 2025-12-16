package ru.prohor.universe.bobafett.feature.holidays.status;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.prohor.universe.bobafett.command.CancelCommandHandler;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusHandler;

import java.time.LocalDate;

@Service
public class WaitCustomHolidayName implements ValuedStatusHandler<String, String> {
    private final MongoRepository<CustomHoliday> customHolidaysRepository;
    private final CancelCommandHandler cancelCommandHandler;

    public WaitCustomHolidayName(
            MongoRepository<CustomHoliday> customHolidaysRepository,
            CancelCommandHandler cancelCommandHandler
    ) {
        this.customHolidaysRepository = customHolidaysRepository;
        this.cancelCommandHandler = cancelCommandHandler;
    }

    @Override
    public String key() {
        return "holidays/wait-name";
    }

    @Override
    public boolean handle(String value, Update update, FeedbackExecutor feedbackExecutor) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return true;
        }
        long chatId = update.getMessage().getChatId();
        String customHolidayName = update.getMessage().getText();
        if (customHolidayName.equals(cancelCommandHandler.command())) {
            feedbackExecutor.sendMessage(chatId, "Создание собственного праздника отменено");
            return false;
        }

        if (customHolidayName.length() > 50) {
            feedbackExecutor.sendMessage(chatId, lengthLimit(customHolidayName.length()));
            return false;
        }

        CustomHoliday customHoliday = createCustomHoliday(chatId, value, customHolidayName);
        customHolidaysRepository.save(customHoliday);
        feedbackExecutor.sendMessage(chatId, "Праздник \"" + customHolidayName + "\" успешно добавлен");
        return false;
    }

    private String lengthLimit(int length) {
        return "Название праздника превышает лимит в 50 символов - оно содержит " +
                length + " знаков. Напишите название заново или отмените " +
                "добавление с помощью команды " + cancelCommandHandler.command();
    }

    private CustomHoliday createCustomHoliday(long chatId, String date, String name) {
        LocalDate localDate = LocalDate.parse(date);
        return new CustomHoliday(
                ObjectId.get(),
                chatId,
                localDate.getMonthValue(),
                localDate.getDayOfMonth(),
                name
        );
    }
}
