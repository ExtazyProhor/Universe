package ru.prohor.universe.bobafett.feature.currency.callback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.PaginationResult;
import ru.prohor.universe.jocasta.core.collections.Paginator;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.JsonCallbackHandler;
import ru.prohor.universe.jocasta.tgbots.util.InlineKeyboardUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChangeSelectedCurrenciesCallback extends JsonCallbackHandler<ChangeSelectedCurrenciesCallback.Payload> {
    private static final String MESSAGE = "Нажимайте на валюту, чтобы добавить/убрать ее из рассылки";
    private static final int CURRENCIES_PER_PAGE = 8;
    private static final int FIRST_PAGE = 0;

    private final BobaFettUserService bobaFettUserService;

    public ChangeSelectedCurrenciesCallback(
            ObjectMapper objectMapper,
            BobaFettUserService bobaFettUserService
    ) {
        super(Callbacks.CHANGE_SELECTED_CURRENCIES, Payload.class, objectMapper);
        this.bobaFettUserService = bobaFettUserService;
    }

    @Override
    protected void handle(Payload payload, MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        EditMessageText editMessage = EditMessageText.builder().build();
        editMessage.setChatId(message.getChatId());
        editMessage.setMessageId(message.getMessageId());
        editMessage.setText(MESSAGE);

        switch (payload.option) {
            case SWITCH -> {
                payload.chosenCurrencies.get().flip(payload.chosenCurrency.get());
                editMessage.setReplyMarkup(makeKeyboard(payload.page, payload.chosenCurrencies.get()));
            }
            case INITIALIZE -> {
                BobaFettUser user = bobaFettUserService.ensureFindByChatId(message.getChatId());
                editMessage.setReplyMarkup(makeKeyboard(FIRST_PAGE, createEnabledCurrenciesBitSet(user)));
            }
            case SWIPE -> editMessage.setReplyMarkup(makeKeyboard(payload.page, payload.chosenCurrencies.get()));
            case CONFIRM -> {
                List<Currency> chosenCurrencies = payload.chosenCurrencies.get().stream()
                        .mapToObj(Currency.CURRENCIES_BY_INDEX::get)
                        .toList();
                bobaFettUserService.safeUpdate(
                        message.getChatId(),
                        user -> user.toBuilder().currencySubscriptionOptions(
                                user.currencySubscriptionOptions().toBuilder()
                                        .selectedCurrencies(chosenCurrencies)
                                        .build()
                        ).build()
                );
                editMessage.setText(getResultMessage(chosenCurrencies));
            }
        }
        feedbackExecutor.editMessageText(editMessage);
    }

    public String initialCallback() {
        return makeCallback(new Payload(Opt.empty(), FIRST_PAGE, Option.INITIALIZE, Opt.empty()));
    }

    private String getResultMessage(List<Currency> chosenCurrencies) {
        return "Теперь вы будете получать курс у следующих валют:\n\n" +
                chosenCurrencies.stream().map(c -> c.code + " " + c.flag)
                        .collect(Collectors.joining(", "));
    }

    private BitSet createEnabledCurrenciesBitSet(BobaFettUser user) {
        BitSet bitSet = new BitSet();
        user.currencySubscriptionOptions().selectedCurrencies().forEach(currency -> bitSet.set(currency.index));
        return bitSet;
    }

    private InlineKeyboardMarkup makeKeyboard(int page, BitSet bitSet) {
        PaginationResult<Currency> pagination = Paginator.richPaginateOrLastPage(
                Currency.CURRENCIES_FOR_RATES,
                page,
                CURRENCIES_PER_PAGE
        );

        page = pagination.page();
        int lastPage = pagination.lastPage();
        int pages = lastPage + 1;

        List<List<String>> buttonsText = new ArrayList<>();
        List<List<String>> buttonsCallback = new ArrayList<>();


        for (Currency currency : pagination.values()) {
            String text = (bitSet.get(currency.index) ? "🟢" : "🔴") + " " +
                    currency.flag + " " + currency.code + " " + currency.russianName;
            String callback = makeCallback(new Payload(
                    Opt.of(bitSet),
                    page,
                    Option.SWITCH,
                    Opt.of(currency.index)
            ));
            buttonsText.add(List.of(text));
            buttonsCallback.add(List.of(callback));
        }

        if (pages > 1) {
            int previous = page == FIRST_PAGE ? lastPage : page - 1;
            int next = page == lastPage ? FIRST_PAGE : page + 1;

            buttonsText.add(List.of("❮", (page + 1) + "/" + pages, "❯"));
            buttonsCallback.add(List.of(
                    makeCallback(new Payload(Opt.of(bitSet), previous, Option.SWIPE, Opt.empty())),
                    Callbacks.BLANK,
                    makeCallback(new Payload(Opt.of(bitSet), next, Option.SWIPE, Opt.empty()))
            ));
        }
        buttonsText.add(List.of("Сохранить"));
        buttonsCallback.add(List.of(makeCallback(new Payload(Opt.of(bitSet), page, Option.CONFIRM, Opt.empty()))));
        return InlineKeyboardUtils.getInlineKeyboard(buttonsText, buttonsCallback);
    }

    /**
     * Warning! {@code Payload} fields is order dependent
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record Payload(
            Opt<BitSet> chosenCurrencies,
            Integer page,
            Option option,
            Opt<Integer> chosenCurrency
    ) {}

    /**
     * Warning! {@code Option} fields is order dependent
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum Option {
        SWITCH,
        CONFIRM,
        INITIALIZE,
        SWIPE
    }
}
