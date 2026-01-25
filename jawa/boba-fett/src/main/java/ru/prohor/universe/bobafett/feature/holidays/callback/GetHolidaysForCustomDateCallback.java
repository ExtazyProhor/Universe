package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.feature.holidays.DistributionDataProvider;
import ru.prohor.universe.bobafett.feature.holidays.HolidaysService;
import ru.prohor.universe.bobafett.feature.holidays.date.ChangeDateKeyboardUtils;
import ru.prohor.universe.bobafett.feature.holidays.date.Payload;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;

import java.time.LocalDate;

@Service
public class GetHolidaysForCustomDateCallback extends JsonCallbackHandler<Payload> {
    private static final String CANCEL_GET_HOLIDAYS = "Отменить";
    private static final String CHOOSE_DATE_MESSAGE = "Выберите дату праздников (этого или следующего года)";

    private final DistributionDataProvider distributionDataProvider;
    private final HolidaysService holidaysService;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;

    public GetHolidaysForCustomDateCallback(
            DistributionDataProvider distributionDataProvider,
            HolidaysService holidaysService,
            ObjectMapper objectMapper,
            MongoRepository<CustomHoliday> customHolidaysRepository
    ) {
        super(Callbacks.GET_HOLIDAYS_FOR_CUSTOM_DATE, Payload.class, objectMapper);
        this.distributionDataProvider = distributionDataProvider;
        this.holidaysService = holidaysService;
        this.customHolidaysRepository = customHolidaysRepository;
    }

    @Override
    protected void handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();

        switch (payload.option()) {
            case APPLY -> editMessageByHolidays(messageId, chatId, payload.date(), feedbackExecutor);
            case CHANGE_DATE -> {
                LocalDate date = fixDate(ChangeDateKeyboardUtils.calculateChangedDate(payload));
                editMessageByChooseHolidaysDate(messageId, chatId, date, feedbackExecutor);
            }
            case CANCEL -> feedbackExecutor.editMessageText(
                    chatId,
                    messageId,
                    "Если заходите узнать праздники на определенную дату, используйте команду " + Commands.HOLIDAYS
            );
        }
    }

    public void editMessageByChooseHolidaysDate(
            int messageId,
            long chatId,
            LocalDate date,
            FeedbackExecutor feedbackExecutor
    ) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                CHOOSE_DATE_MESSAGE,
                ChangeDateKeyboardUtils.makeKeyboardForDate(date, CANCEL_GET_HOLIDAYS, this::makeCallback, true)
        );
    }

    private void editMessageByHolidays(int messageId, long chatId, LocalDate date, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                holidaysService.getHolidaysMessageForDate(
                        date,
                        LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID).getYear(),
                        distributionDataProvider.findCustomHolidays(customHolidaysRepository, chatId, date)
                )
        );
    }

    private LocalDate fixDate(LocalDate date) {
        int thisYear = LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID).getYear();
        if (date.getYear() > thisYear + 1)
            return date.withYear(thisYear + 1);
        if (date.getYear() < thisYear)
            return date.withYear(thisYear);
        return date;
    }
}
