package ru.prohor.universe.bobafett.feature.currency.callback;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import ru.prohor.universe.bobafett.callback.Callbacks;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.feature.currency.CurrencyMessageGenerator;
import ru.prohor.universe.bobafett.feature.currency.HintAboutChangingSelectedCurrencyService;
import ru.prohor.universe.bobafett.feature.currency.LatestAvailableCurrencyRatesProvider;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.callback.CallbackHandler;

@Service
public class GetCurrencyCallback implements CallbackHandler {
    private final HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService;
    private final BobaFettUserService bobaFettUserService;
    private final MongoRepository<BobaFettUser> usersRepository;
    private final MongoRepository<CurrencyRate> currencyRatesRepository;
    private final CurrencyMessageGenerator currencyMessageGenerator;
    private final LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider;

    public GetCurrencyCallback(
            HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService,
            BobaFettUserService bobaFettUserService,
            MongoRepository<BobaFettUser> usersRepository,
            MongoRepository<CurrencyRate> currencyRatesRepository,
            CurrencyMessageGenerator currencyMessageGenerator,
            LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider
    ) {
        this.hintAboutChangingSelectedCurrencyService = hintAboutChangingSelectedCurrencyService;
        this.bobaFettUserService = bobaFettUserService;
        this.usersRepository = usersRepository;
        this.currencyRatesRepository = currencyRatesRepository;
        this.currencyMessageGenerator = currencyMessageGenerator;
        this.latestAvailableCurrencyRatesProvider = latestAvailableCurrencyRatesProvider;
    }

    @Override
    public String callback() {
        return Callbacks.GET_CURRENCY;
    }

    @Override
    public void handle(MaybeInaccessibleMessage message, FeedbackExecutor feedbackExecutor) {
        Long chatId = message.getChatId();
        CurrencyRate latestCurrencyRate = latestAvailableCurrencyRatesProvider.getLatestAvailableCurrencyRates();

        BobaFettUser user = usersRepository.withTransaction(tx -> {
            BobaFettUser old = bobaFettUserService.ensureFindByChatId(tx, chatId);
            Opt<ObjectId> lastReceivedCurrencyRates = old.currencySubscriptionOptions().lastReceivedCurrencyRates();
            boolean currencyRateChanged = !latestCurrencyRate.id().equals(lastReceivedCurrencyRates.orElseNull());
            if (currencyRateChanged) {
                CurrencySubscriptionOptions options = old.currencySubscriptionOptions().toBuilder()
                        .lastReceivedCurrencyRates(Opt.of(latestCurrencyRate.id()))
                        .build();
                BobaFettUser updated = old.toBuilder().currencySubscriptionOptions(options).build();
                tx.save(updated);
            }
            return old; // для доступа к старым currencySubscriptionOptions.lastReceivedCurrencyRates
        });

        Opt<CurrencyRate> lastUsersRates = user.currencySubscriptionOptions().lastReceivedCurrencyRates()
                .map(currencyRatesRepository::ensuredFindById);
        feedbackExecutor.editMessageText(
                chatId,
                message.getMessageId(),
                currencyMessageGenerator.getCurrencyMessageFor(user, latestCurrencyRate, lastUsersRates)
        );
        hintAboutChangingSelectedCurrencyService.executeHint(user, feedbackExecutor);
    }
}
