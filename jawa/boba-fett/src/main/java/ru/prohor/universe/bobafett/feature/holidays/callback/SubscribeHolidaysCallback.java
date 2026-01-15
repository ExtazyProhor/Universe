package ru.prohor.universe.bobafett.feature.holidays.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.bobafett.data.pojo.HolidaysSubscriptionOptions;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.time.LocalTime;
import java.util.List;

@Service
public class SubscribeHolidaysCallback extends JsonCallbackHandler<SubscribeHolidaysCallback.Payload> {
    private static final String SETTINGS_MESSAGE =
            "Выберите время ежедневной рассылки и день праздников, относительно даты рассылки";
    private static final List<String> TIME_TEXT = List.of("-1:00", "-0:15", "+0:15", "+1:00");
    private static final String[] INDENT_TEXT = {"того же дня", "следующего дня", "после-следующего дня"};
    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;
    private static final int DEFAULT_INDENT = 0;

    private final BobaFettUserService bobaFettUserService;
    private final MongoRepository<BobaFettUser> usersRepository;

    public SubscribeHolidaysCallback(
            ObjectMapper objectMapper,
            BobaFettUserService bobaFettUserService,
            MongoRepository<BobaFettUser> usersRepository
    ) {
        super(Callbacks.SUBSCRIBE_HOLIDAYS, Payload.class, objectMapper);
        this.bobaFettUserService = bobaFettUserService;
        this.usersRepository = usersRepository;
    }

    @Override
    protected boolean handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        switch (payload.option) {
            case SETTINGS -> {
                BobaFettUser user = bobaFettUserService.ensureFindByChatId(usersRepository, chatId);
                HolidaysSubscriptionOptions options = user.holidaysSubscriptionOptions().orElseThrow();
                settingSubscription(
                        feedbackExecutor,
                        chatId,
                        messageId,
                        options.dailyDistributionTime().hour(),
                        options.dailyDistributionTime().minute(),
                        options.indentationOfDays()
                );
            }
            case SUBSCRIBE -> subscribeControl(true, chatId, messageId, feedbackExecutor);
            case UNSUBSCRIBE -> subscribeControl(false, chatId, messageId, feedbackExecutor);
            case CHANGE_OPTIONS -> settingSubscription(
                    feedbackExecutor,
                    chatId,
                    messageId,
                    payload.hour,
                    payload.minute,
                    payload.indent
            );
            case CONFIRM -> {
                HolidaysSubscriptionOptions options = new HolidaysSubscriptionOptions(
                        new DistributionTime(payload.hour, payload.minute),
                        payload.indent,
                        true
                );
                bobaFettUserService.safeUpdate(
                        chatId,
                        user -> user.toBuilder().holidaysSubscriptionOptions(Opt.of(options)).build()
                );
                feedbackExecutor.editMessageText(
                        chatId,
                        messageId,
                        "Настройки успешно применены. В " + payload.hour + ":" +
                                (payload.minute == 0 ? "00" : payload.minute) +
                                " каждый день будет приходить " + "список праздников " + INDENT_TEXT[payload.indent]
                );
            }
        }
        return false;
    }

    public void sendMenu(long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        boolean subscribed = bobaFettUserService.findByChatId(usersRepository, chatId)
                .flatMapO(BobaFettUser::holidaysSubscriptionOptions)
                .map(HolidaysSubscriptionOptions::subscriptionIsActive)
                .orElse(false);
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                "Что вы хотите сделать?",
                subscribed ? menuKeyboardSubscribed : menuKeyboardUnsubscribed
        );
    }

    private void subscribeControl(boolean isSubscribe, long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        usersRepository.withTransaction(tx -> {
            BobaFettUser user = bobaFettUserService.ensureFindByChatId(tx, chatId);
            Opt<HolidaysSubscriptionOptions> options = user.holidaysSubscriptionOptions();
            if (options.isEmpty()) {
                settingSubscription(
                        feedbackExecutor,
                        chatId,
                        messageId,
                        DEFAULT_HOUR,
                        DEFAULT_MINUTE,
                        DEFAULT_INDENT
                );
                return;
            }
            options = options.map(it -> it.toBuilder().subscriptionIsActive(isSubscribe).build());
            user = user.toBuilder().holidaysSubscriptionOptions(options).build();
            tx.save(user);
            feedbackExecutor.editMessageText(
                    chatId,
                    messageId,
                    "Теперь вы " + (isSubscribe ? "" : "не ") + "будете получать рассылку праздников"
            );
        });
    }

    private void settingSubscription(
            FeedbackExecutor feedbackExecutor,
            long chatId,
            int messageId,
            int hour,
            int minute,
            int indent
    ) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                SETTINGS_MESSAGE,
                makeSettingsKeyboard(LocalTime.of(hour, minute), indent)
        );
    }

    private InlineKeyboardMarkup makeSettingsKeyboard(LocalTime time, int indent) {
        return InlineKeyboardUtils.getInlineKeyboard(
                List.of(
                        List.of("Установить время: " + DateTimeUtil.formatWithoutMillis(time)),
                        TIME_TEXT,
                        List.of("тот же день " + indentText(indent, 0)),
                        List.of("день после " + indentText(indent, 1)),
                        List.of("через день " + indentText(indent, 2)),
                        List.of("Применить")
                ),
                List.of(
                        List.of(Callbacks.BLANK),
                        List.of(
                                time(time.minusHours(1), indent),
                                time(time.minusMinutes(15), indent),
                                time(time.plusMinutes(15), indent),
                                time(time.plusHours(1), indent)
                        ),
                        List.of(indent(time, indent, 0)),
                        List.of(indent(time, indent, 1)),
                        List.of(indent(time, indent, 2)),
                        List.of(makeCallback(Payload.create(Option.CONFIRM, time, indent)))
                )
        );
    }

    private String indentText(int indent, int target) {
        return indent == target ? "✅" : "◽";
    }

    private String time(LocalTime time, int indent) {
        return makeCallback(Payload.create(Option.CHANGE_OPTIONS, time, indent));
    }

    private String indent(LocalTime time, int indent, int target) {
        if (indent == target)
            return Callbacks.BLANK;
        return makeCallback(Payload.create(Option.CHANGE_OPTIONS, time, target));
    }

    private final InlineKeyboardMarkup menuKeyboardSubscribed = InlineKeyboardUtils.getColumnInlineKeyboard(
            List.of(
                    "Настроить время рассылки и дату праздников",
                    "Отписаться от рассылки"
            ),
            List.of(
                    makeCallback(Payload.create(Option.SETTINGS)),
                    makeCallback(Payload.create(Option.UNSUBSCRIBE))
            )
    );
    private final InlineKeyboardMarkup menuKeyboardUnsubscribed = InlineKeyboardUtils.getColumnInlineKeyboard(
            List.of("Подписаться на рассылку"),
            List.of(makeCallback(Payload.create(Option.SUBSCRIBE)))
    );

    protected record Payload(
            @JsonProperty("a")
            Option option,
            @JsonProperty("b")
            Integer hour,
            @JsonProperty("c")
            Integer minute,
            @JsonProperty("d")
            Integer indent
    ) {
        private static Payload create(Option option) {
            return new Payload(option, null, null, null);
        }

        private static Payload create(Option option, LocalTime time, int indent) {
            return new Payload(option, time.getHour(), time.getMinute(), indent);
        }
    }

    protected enum Option {
        @JsonProperty("a")
        SETTINGS,
        @JsonProperty("b")
        SUBSCRIBE,
        @JsonProperty("c")
        UNSUBSCRIBE,
        @JsonProperty("d")
        CHANGE_OPTIONS,
        @JsonProperty("e")
        CONFIRM
    }
}
