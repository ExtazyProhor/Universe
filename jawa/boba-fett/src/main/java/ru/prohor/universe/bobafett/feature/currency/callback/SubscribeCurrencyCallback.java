package ru.prohor.universe.bobafett.feature.currency.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.command.Commands;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
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
public class SubscribeCurrencyCallback extends JsonCallbackHandler<SubscribeCurrencyCallback.Payload> {
    private static final String SETTINGS_MESSAGE = "Выберите время ежедневной рассылки (по МСК)";
    private static final List<String> MAJOR_HOURS_TEXT = List.of("-6 ч", "+6 ч");
    private static final List<String> MIDDLE_HOURS_TEXT = List.of("-3 ч", "+3 ч");
    private static final List<String> MINOR_HOURS_TEXT = List.of("-1 ч", "+1 ч");
    private static final List<String> MINUTES_TEXT = List.of("-15 мин", "+15 мин");
    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;

    private final BobaFettUserService bobaFettUserService;
    private final MongoRepository<BobaFettUser> usersRepository;

    public SubscribeCurrencyCallback(
            ObjectMapper objectMapper,
            BobaFettUserService bobaFettUserService,
            MongoRepository<BobaFettUser> usersRepository
    ) {
        super(Callbacks.SUBSCRIBE_CURRENCY, Payload.class, objectMapper);
        this.bobaFettUserService = bobaFettUserService;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        long chatId = message.getChatId();
        int messageId = message.getMessageId();
        switch (payload.option) {
            case SETTINGS -> {
                BobaFettUser user = bobaFettUserService.ensureFindByChatId(usersRepository, chatId);
                CurrencySubscriptionOptions options = user.currencySubscriptionOptions().orElseThrow();
                settingSubscription(
                        feedbackExecutor,
                        chatId,
                        messageId,
                        options.dailyDistributionTime().hour(),
                        options.dailyDistributionTime().minute()
                );
            }
            case SUBSCRIBE -> subscribeControl(true, chatId, messageId, feedbackExecutor);
            case UNSUBSCRIBE -> subscribeControl(false, chatId, messageId, feedbackExecutor);
            case CHANGE_OPTIONS -> settingSubscription(
                    feedbackExecutor,
                    chatId,
                    messageId,
                    payload.hour,
                    payload.minute
            );
            case CONFIRM -> {
                bobaFettUserService.safeUpdate(
                        chatId,
                        user -> {
                            CurrencySubscriptionOptions options = new CurrencySubscriptionOptions(
                                    new DistributionTime(payload.hour, payload.minute),
                                    true,
                                    user.currencySubscriptionOptions()
                                            .map(CurrencySubscriptionOptions::selectedCurrencies)
                                            .flattenO()
                            );
                            return user.toBuilder().currencySubscriptionOptions(Opt.of(options)).build();
                        }
                );
                feedbackExecutor.editMessageText(
                        chatId,
                        messageId,
                        "Настройки успешно применены. В " + payload.hour + ":" +
                                (payload.minute == 0 ? "00" : payload.minute) +
                                " по МСК каждый день будет приходить актуальный курс валют"
                );
            }
        }
    }

    public void sendMenu(long chatId, int messageId, FeedbackExecutor feedbackExecutor) {
        boolean subscribed = bobaFettUserService.findByChatId(usersRepository, chatId)
                .flatMapO(BobaFettUser::currencySubscriptionOptions)
                .map(CurrencySubscriptionOptions::subscriptionIsActive)
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
            Opt<CurrencySubscriptionOptions> options = user.currencySubscriptionOptions();
            if (options.isEmpty()) {
                settingSubscription(
                        feedbackExecutor,
                        chatId,
                        messageId,
                        DEFAULT_HOUR,
                        DEFAULT_MINUTE
                );
                return;
            }
            options = options.map(it -> it.toBuilder().subscriptionIsActive(isSubscribe).build());
            user = user.toBuilder().currencySubscriptionOptions(options).build();
            tx.save(user);
            String message = "Теперь вы " + (isSubscribe ? "" : "не ") + "будете получать рассылку курсов валют";
            if (isSubscribe) {
                message = message + ". Настройте время рассылки, вызвав еще раз команду " + Commands.CURRENCY;
            }

            feedbackExecutor.editMessageText(
                    chatId,
                    messageId,
                    message
            );
        });
    }

    private void settingSubscription(
            FeedbackExecutor feedbackExecutor,
            long chatId,
            int messageId,
            int hour,
            int minute
    ) {
        feedbackExecutor.editMessageText(
                chatId,
                messageId,
                SETTINGS_MESSAGE,
                makeSettingsKeyboard(LocalTime.of(hour, minute))
        );
    }

    private InlineKeyboardMarkup makeSettingsKeyboard(LocalTime time) {
        return InlineKeyboardUtils.getInlineKeyboard(
                List.of(
                        List.of("Установить время: " + DateTimeUtil.timeWithoutMillis(time)),
                        MAJOR_HOURS_TEXT,
                        MIDDLE_HOURS_TEXT,
                        MINOR_HOURS_TEXT,
                        MINUTES_TEXT,
                        List.of("Применить")
                ),
                List.of(
                        List.of(Callbacks.BLANK),
                        makeTimeRow(time.minusHours(6), time.plusHours(6)),
                        makeTimeRow(time.minusHours(3), time.plusHours(3)),
                        makeTimeRow(time.minusHours(1), time.plusHours(1)),
                        makeTimeRow(time.minusMinutes(15), time.plusMinutes(15)),
                        List.of(makeCallback(Payload.create(Option.CONFIRM, time)))
                )
        );
    }

    private List<String> makeTimeRow(LocalTime time1, LocalTime time2) {
        return List.of(time(time1), time(time2));
    }

    private String time(LocalTime time) {
        return makeCallback(Payload.create(Option.CHANGE_OPTIONS, time));
    }

    private final InlineKeyboardMarkup menuKeyboardSubscribed = InlineKeyboardUtils.getColumnInlineKeyboard(
            List.of(
                    "Настроить время рассылки",
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
            Integer minute
    ) {
        private static Payload create(Option option) {
            return new Payload(option, null, null);
        }

        private static Payload create(Option option, LocalTime time) {
            return new Payload(option, time.getHour(), time.getMinute());
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
