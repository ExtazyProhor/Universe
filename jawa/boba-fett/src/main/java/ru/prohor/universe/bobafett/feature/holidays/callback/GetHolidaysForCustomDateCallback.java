package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.data.BobaFettRepositoryHelper;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.feature.holidays.HolidaysService;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.List;

@Service
public class GetHolidaysForCustomDateCallback extends JsonCallbackHandler<GetHolidaysForCustomDateCallback.Payload> {
    private static final String CHOOSE_DATE_MESSAGE = "Выберите дату праздников (этого или следующего года)";
    private static final List<String> ACCEPT_ROW = List.of("Выбрать эту дату");
    private static final List<String> DAY_ROW_TEXT = List.of("-5 дней", "-1 день", "+1 день", "+5 дней");
    private static final List<String> MONTH_ROW_TEXT = List.of("-3 месяца", "-1 месяц", "+1 месяц", "+3 месяца");

    private final HolidaysService holidaysService;
    private final BlankCallback blankCallback;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;

    public GetHolidaysForCustomDateCallback(
            HolidaysService holidaysService,
            ObjectMapper objectMapper,
            BlankCallback blankCallback,
            MongoRepository<CustomHoliday> customHolidaysRepository
    ) {
        super("holidays/get", Payload.class, objectMapper);
        this.holidaysService = holidaysService;
        this.blankCallback = blankCallback;
        this.customHolidaysRepository = customHolidaysRepository;
    }

    @Override
    protected boolean handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        LocalDate date = payload.date;

        switch (payload.option) {
            case APPLY -> editMessageByHolidays(messageId, chatId, date, feedbackExecutor);
            case CHANGE_DATE -> {
                Period period = switch (payload.timeUnit) {
                    case DAY -> Period.days(payload.count);
                    case MONTH -> Period.months(payload.count);
                };
                date = fixDate(payload.increase ? date.plus(period) : date.minus(period));
                editMessageByChooseHolidaysDate(messageId, chatId, date, feedbackExecutor);
            }
        }
        return false;
    }

    public void editMessageByChooseHolidaysDate(
            int messageId,
            long chatId,
            LocalDate date,
            FeedbackExecutor feedbackExecutor
    ) {
        feedbackExecutor.editMessageText(chatId, messageId, CHOOSE_DATE_MESSAGE, makeKeyboardForDate(date));
    }

    private InlineKeyboardMarkup makeKeyboardForDate(LocalDate date) {
        return InlineKeyboardUtils.getInlineKeyboard(
                List.of(
                        List.of(DateTimeUtil.russianFullDate(date)),
                        DAY_ROW_TEXT,
                        MONTH_ROW_TEXT,
                        ACCEPT_ROW
                ),
                List.of(
                        List.of(blankCallback.callback()),
                        List.of(
                                change(date, TimeUnit.DAY, 5, false),
                                change(date, TimeUnit.DAY, 1, false),
                                change(date, TimeUnit.DAY, 1, true),
                                change(date, TimeUnit.DAY, 5, true)
                        ),
                        List.of(
                                change(date, TimeUnit.MONTH, 3, false),
                                change(date, TimeUnit.MONTH, 1, false),
                                change(date, TimeUnit.MONTH, 1, true),
                                change(date, TimeUnit.MONTH, 3, true)
                        ),
                        List.of(apply(date))
                )
        );
    }

    private String change(LocalDate date, TimeUnit timeUnit, Integer count, Boolean increase) {
        return makeCallback(new Payload(Option.CHANGE_DATE, date, timeUnit, count, increase));
    }

    private String apply(LocalDate date) {
        return makeCallback(new Payload(Option.APPLY, date, null, null, null));
    }

    private void editMessageByHolidays(int messageId, long chatId, LocalDate date, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                holidaysService.getHolidaysMessageForDate(
                        date,
                        LocalDate.now(DateTimeUtil.zoneMoscow()).getYear(),
                        BobaFettRepositoryHelper.findCustomHolidays(customHolidaysRepository, chatId, date)
                )
        );
    }

    private LocalDate fixDate(LocalDate date) {
        int thisYear = LocalDate.now(DateTimeUtil.zoneMoscow()).getYear();
        if (date.getYear() > thisYear + 1)
            return date.withYear(thisYear + 1);
        if (date.getYear() < thisYear)
            return date.withYear(thisYear);
        return date;
    }

    protected record Payload(
            @JsonProperty("a")
            Option option,
            @JsonProperty("b")
            LocalDate date,
            @JsonProperty("c")
            TimeUnit timeUnit,
            @JsonProperty("d")
            Integer count,
            @JsonProperty("e")
            Boolean increase
    ) {}

    protected enum Option {
        @JsonProperty("a")
        APPLY,
        @JsonProperty("b")
        CHANGE_DATE,
    }

    protected enum TimeUnit {
        @JsonProperty("a")
        DAY,
        @JsonProperty("b")
        MONTH
    }
}
