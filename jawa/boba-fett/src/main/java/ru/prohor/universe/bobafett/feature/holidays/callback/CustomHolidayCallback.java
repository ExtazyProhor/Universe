package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.CustomHoliday;
import ru.prohor.universe.bobafett.feature.holidays.CustomHolidaysService;
import ru.prohor.universe.jocasta.core.collections.PaginationResult;
import ru.prohor.universe.jocasta.core.collections.Paginator;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomHolidayCallback extends JsonCallbackHandler<CustomHolidayCallback.Payload> {
    private static final Comparator<CustomHoliday> CUSTOM_HOLIDAYS_COMPARATOR = Comparator
            .comparingInt(CustomHoliday::month)
            .thenComparingInt(CustomHoliday::dayOfMonth)
            .thenComparing(CustomHoliday::id);
    private static final String CHOOSE_HOLIDAY_TO_DELETE = "Выберите праздник, который нужно удалить";
    private static final int FIRST_PAGE = 0;
    private static final int HOLIDAYS_PER_PAGE = 7;
    private static final int MAX_BUTTON_TEXT = 35;

    private final ChooseCustomHolidayDateCallback chooseCustomHolidayDateCallback;
    private final CustomHolidaysService customHolidaysService;

    public CustomHolidayCallback(
            ObjectMapper objectMapper,
            ChooseCustomHolidayDateCallback chooseCustomHolidayDateCallback,
            CustomHolidaysService customHolidaysService
    ) {
        super(Callbacks.CUSTOM_HOLIDAY, Payload.class, objectMapper);
        this.chooseCustomHolidayDateCallback = chooseCustomHolidayDateCallback;
        this.customHolidaysService = customHolidaysService;
    }

    public final InlineKeyboardMarkup keyboard = InlineKeyboardUtils.getColumnInlineKeyboard(
            List.of(
                    "Создать",
                    "Удалить",
                    "Просмотреть список"
            ),
            List.of(
                    makeCallback(Payload.create(Option.CREATE)),
                    makeCallback(Payload.create(Option.DELETE)),
                    makeCallback(Payload.create(Option.LIST))
            )
    );

    @Override
    protected boolean handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        switch (payload.option) {
            case CREATE -> createCustomHoliday(chatId, messageId, feedbackExecutor);
            case DELETE -> deleteCustomHoliday(FIRST_PAGE, chatId, messageId, feedbackExecutor);
            case LIST -> listCustomHoliday(FIRST_PAGE, chatId, messageId, feedbackExecutor);
            case CANCEL -> cancelDeletingCustomHoliday(chatId, messageId, feedbackExecutor);
            case SCROLL_LIST -> listCustomHoliday(payload.nextPage, chatId, messageId, feedbackExecutor);
            case SCROLL_DELETE -> deleteCustomHoliday(payload.nextPage, chatId, messageId, feedbackExecutor);
            case DELETE_BY_ID -> deleteCustomHolidayById(payload.customHolidayId, chatId, messageId, feedbackExecutor);
        }
        return false;
    }

    private static String getCustomHolidayDescription(CustomHoliday holiday) {
        String month = DateTimeUtil.russianMonth(holiday.month());
        return holiday.dayOfMonth() + " " + month + " - " + holiday.holidayName();
    }

    private void createCustomHoliday(long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(chooseCustomHolidayDateCallback.createEditMessage(
                LocalDate.now(DateTimeUtil.MOSCOW_ZONE_ID),
                chatId,
                messageId
        ));
    }

    private void deleteCustomHolidayById(String id, long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        Opt<CustomHoliday> deleted = customHolidaysService.deleteCustomHoliday(id);
        String message = deleted.isPresent()
                ? "Праздник " + getCustomHolidayDescription(deleted.get()) + " удален"
                : "Праздник уже удален";
        feedbackExecutor.editMessageText(chatId, messageId, message);
    }

    private void deleteCustomHoliday(int page, long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        List<CustomHoliday> customHolidays = new ArrayList<>(customHolidaysService.findCustomHolidays(chatId));
        if (customHolidays.isEmpty()) {
            hasNotHolidays(chatId, messageId, feedbackExecutor);
            return;
        }

        customHolidays.sort(CUSTOM_HOLIDAYS_COMPARATOR);
        PaginationResult<CustomHoliday> pagination = Paginator.richPaginateOrLastPage(
                customHolidays, page, HOLIDAYS_PER_PAGE
        );

        List<List<String>> buttonsText = new ArrayList<>();
        List<List<String>> buttonsCallback = new ArrayList<>();

        for (CustomHoliday holiday : pagination.values()) {
            String fullHoliday = getCustomHolidayDescription(holiday);
            fullHoliday = fullHoliday.length() < MAX_BUTTON_TEXT
                    ? fullHoliday
                    : fullHoliday.substring(0, MAX_BUTTON_TEXT - 3) + "...";
            buttonsText.add(List.of(fullHoliday));
            buttonsCallback.add(List.of(makeCallback(new Payload(Option.DELETE_BY_ID, holiday.id().toString(), null))));
        }
        buttonsText.add(List.of("❌ Отменить удаление ❌"));
        buttonsCallback.add(List.of(makeCallback(Payload.create(Option.CANCEL))));

        int pages = pagination.lastPage() + 1;
        InlineKeyboardMarkup keyboard = pages > 1
                ? makeKeyboardForScrolling(pagination, buttonsText, buttonsCallback, Option.SCROLL_DELETE)
                : InlineKeyboardUtils.getInlineKeyboard(buttonsText, buttonsCallback);

        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                CHOOSE_HOLIDAY_TO_DELETE,
                keyboard
        );
    }

    private void listCustomHoliday(int page, long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        List<CustomHoliday> customHolidays = new ArrayList<>(customHolidaysService.findCustomHolidays(chatId));
        if (customHolidays.isEmpty()) {
            hasNotHolidays(chatId, messageId, feedbackExecutor);
            return;
        }

        customHolidays.sort(CUSTOM_HOLIDAYS_COMPARATOR);
        PaginationResult<CustomHoliday> pagination = Paginator.richPaginateOrLastPage(
                customHolidays, page, HOLIDAYS_PER_PAGE
        );

        StringBuilder message = new StringBuilder();
        for (CustomHoliday holiday : pagination.values()) {
            message.append("- ").append(getCustomHolidayDescription(holiday)).append("\n");
        }

        int pages = pagination.lastPage() + 1;
        if (pages == 0) {
            feedbackExecutor.editMessageText(chatId, messageId, message.toString());
            return;
        }

        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                message.toString(),
                makeKeyboardForScrolling(pagination, new ArrayList<>(), new ArrayList<>(), Option.SCROLL_LIST)
        );
    }

    private InlineKeyboardMarkup makeKeyboardForScrolling(
            PaginationResult<?> pagination,
            List<List<String>> text,
            List<List<String>> callbacks,
            Option option
    ) {
        int page = pagination.page();
        int pages = pagination.lastPage() + 1;
        int previous = page == 0 ? pagination.lastPage() : page - 1;
        int next = page == pagination.lastPage() ? 0 : page + 1;
        if (pages > 1) {
            text.add(List.of("❮", (page + 1) + "/" + pages, "❯"));
            callbacks.add(List.of(
                    makeCallback(new Payload(option, null, previous)),
                    Callbacks.BLANK,
                    makeCallback(new Payload(option, null, next))
            ));
        }
        return InlineKeyboardUtils.getInlineKeyboard(text, callbacks);
    }

    private void hasNotHolidays(long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(chatId, messageId, "У вас еще нет собственных праздников");
    }

    private void cancelDeletingCustomHoliday(long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.editMessageText(chatId, messageId, "Вы отменили удаление праздника");
    }

    protected record Payload(
            @JsonProperty("a")
            Option option,
            @JsonProperty("b")
            String customHolidayId,
            @JsonProperty("c")
            Integer nextPage
    ) {
        private static Payload create(Option option) {
            return new Payload(option, null, null);
        }
    }

    protected enum Option {
        @JsonProperty("a")
        CREATE,
        @JsonProperty("b")
        DELETE,
        @JsonProperty("c")
        LIST,
        @JsonProperty("d")
        CANCEL,
        @JsonProperty("e")
        SCROLL_LIST,
        @JsonProperty("f")
        SCROLL_DELETE,
        @JsonProperty("g")
        DELETE_BY_ID
    }
}
