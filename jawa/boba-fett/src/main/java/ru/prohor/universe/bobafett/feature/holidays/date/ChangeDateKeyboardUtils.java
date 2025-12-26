package ru.prohor.universe.bobafett.feature.holidays.date;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.jocasta.core.collections.common.Bool;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.List;

public class ChangeDateKeyboardUtils {
    private static final List<String> DAY_ROW_TEXT = List.of("-5 дней", "-1 день", "+1 день", "+5 дней");
    private static final List<String> MONTH_ROW_TEXT = List.of("-3 месяца", "-1 месяц", "+1 месяц", "+3 месяца");
    private static final List<String> ACCEPT_ROW = List.of("Выбрать эту дату");

    public static InlineKeyboardMarkup makeKeyboardForDate(
            LocalDate date,
            String cancelText,
            MonoFunction<Payload, String> callbackMaker
    ) {
        return InlineKeyboardUtils.getInlineKeyboard(
                List.of(
                        List.of(DateTimeUtil.russianFullDate(date)),
                        DAY_ROW_TEXT,
                        MONTH_ROW_TEXT,
                        ACCEPT_ROW,
                        List.of(cancelText)
                ),
                List.of(
                        List.of(Callbacks.BLANK),
                        List.of(
                                change(date, TimeUnit.DAY, 5, false, callbackMaker),
                                change(date, TimeUnit.DAY, 1, false, callbackMaker),
                                change(date, TimeUnit.DAY, 1, true, callbackMaker),
                                change(date, TimeUnit.DAY, 5, true, callbackMaker)
                        ),
                        List.of(
                                change(date, TimeUnit.MONTH, 3, false, callbackMaker),
                                change(date, TimeUnit.MONTH, 1, false, callbackMaker),
                                change(date, TimeUnit.MONTH, 1, true, callbackMaker),
                                change(date, TimeUnit.MONTH, 3, true, callbackMaker)
                        ),
                        List.of(apply(date, callbackMaker)),
                        List.of(cancel(callbackMaker))
                )
        );
    }

    public static LocalDate calculateChangedDate(Payload payload) {
        LocalDate date = payload.date();
        Period period = switch (payload.timeUnit()) {
            case DAY -> Period.days(payload.count());
            case MONTH -> Period.months(payload.count());
        };
        return payload.increase().unwrap() ? date.plus(period) : date.minus(period);
    }

    private static String change(
            LocalDate date,
            TimeUnit timeUnit,
            Integer count,
            boolean increase,
            MonoFunction<Payload, String> callbackMaker
    ) {
        return callbackMaker.apply(new Payload(Option.CHANGE_DATE, date, timeUnit, count, Bool.of(increase)));
    }

    private static String apply(LocalDate date, MonoFunction<Payload, String> callbackMaker) {
        return callbackMaker.apply(new Payload(Option.APPLY, date, null, null, null));
    }

    private static String cancel(MonoFunction<Payload, String> callbackMaker) {
        return callbackMaker.apply(new Payload(Option.CANCEL, null, null, null, null));
    }
}
