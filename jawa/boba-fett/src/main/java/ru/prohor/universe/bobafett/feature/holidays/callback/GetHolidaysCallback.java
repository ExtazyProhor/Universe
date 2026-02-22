package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.feature.holidays.DistributionDataProvider;
import ru.prohor.universe.bobafett.feature.holidays.HolidaysService;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetHolidaysCallback extends JsonCallbackHandler<GetHolidaysCallback.Payload> {
    private final DistributionDataProvider distributionDataProvider;
    private final HolidaysService holidaysService;
    private final GetHolidaysForCustomDateCallback getHolidaysForCustomDateCallback;
    private final MongoRepository<CustomHoliday> customHolidaysRepository;

    public GetHolidaysCallback(
            DistributionDataProvider distributionDataProvider,
            HolidaysService holidaysService,
            ObjectMapper objectMapper,
            GetHolidaysForCustomDateCallback getHolidaysForCustomDateCallback,
            MongoRepository<CustomHoliday> customHolidaysRepository
    ) {
        super(Callbacks.GET_HOLIDAYS, Payload.class, objectMapper);
        this.distributionDataProvider = distributionDataProvider;
        this.holidaysService = holidaysService;
        this.getHolidaysForCustomDateCallback = getHolidaysForCustomDateCallback;
        this.customHolidaysRepository = customHolidaysRepository;
    }

    @Override
    protected void handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        LocalDate today = LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID);
        switch (payload.option) {
            case TODAY -> editMessage(messageId, chatId, today, today, feedbackExecutor);
            case TOMORROW -> editMessage(messageId, chatId, today.plusDays(1), today, feedbackExecutor);
            case DAY_AFTER_TOMORROW -> editMessage(messageId, chatId, today.plusDays(2), today, feedbackExecutor);
            case OTHER_DAY -> getHolidaysForCustomDateCallback.editMessageByChooseHolidaysDate(
                    messageId,
                    chatId,
                    today,
                    feedbackExecutor
            );
        }
    }

    public final InlineKeyboardMarkup keyboard = InlineKeyboardUtils.getColumnInlineKeyboard(
            List.of(
                    "Сегодня",
                    "Завтра",
                    "Послезавтра",
                    "Другой день"
            ),
            List.of(
                    makeCallback(new Payload(Option.TODAY)),
                    makeCallback(new Payload(Option.TOMORROW)),
                    makeCallback(new Payload(Option.DAY_AFTER_TOMORROW)),
                    makeCallback(new Payload(Option.OTHER_DAY))
            )
    );

    private void editMessage(
            int messageId,
            long chatId,
            LocalDate date,
            LocalDate today,
            FeedbackExecutor feedbackExecutor
    ) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                holidaysService.getHolidaysMessageForDate(
                        date,
                        today.getYear(),
                        distributionDataProvider.findCustomHolidays(customHolidaysRepository, chatId, date)
                )
        );
    }

    protected record Payload(
            @JsonProperty("a")
            Option option
    ) {}

    protected enum Option {
        @JsonProperty("a")
        TODAY,
        @JsonProperty("b")
        TOMORROW,
        @JsonProperty("c")
        DAY_AFTER_TOMORROW,
        @JsonProperty("d")
        OTHER_DAY
    }
}
