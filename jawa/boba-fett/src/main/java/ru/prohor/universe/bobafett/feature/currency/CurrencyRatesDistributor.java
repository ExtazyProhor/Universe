package ru.prohor.universe.bobafett.feature.currency;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.distribution.DistributionTask;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.util.List;
import java.util.Map;

@Service
public class CurrencyRatesDistributor implements DistributionTask {
    private final HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService;
    private final CurrencyMessageGenerator currencyMessageGenerator;
    private final CurrencyDistributionUsersProvider currencyDistributionUsersProvider;
    private final LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider;
    private final MongoRepository<CurrencyRate> currencyRatesRepository;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;

    public CurrencyRatesDistributor(
            HintAboutChangingSelectedCurrencyService hintAboutChangingSelectedCurrencyService,
            CurrencyMessageGenerator currencyMessageGenerator,
            CurrencyDistributionUsersProvider currencyDistributionUsersProvider,
            LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider,
            MongoRepository<CurrencyRate> currencyRatesRepository,
            MongoRepository<BobaFettUser> bobaFettUsersRepository
    ) {
        this.hintAboutChangingSelectedCurrencyService = hintAboutChangingSelectedCurrencyService;
        this.currencyMessageGenerator = currencyMessageGenerator;
        this.currencyDistributionUsersProvider = currencyDistributionUsersProvider;
        this.latestAvailableCurrencyRatesProvider = latestAvailableCurrencyRatesProvider;
        this.currencyRatesRepository = currencyRatesRepository;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
    }

    @Override
    public void distribute(FeedbackExecutor feedbackExecutor, int hour, int minute) {
        CurrencyRate latestCurrencyRate = latestAvailableCurrencyRatesProvider.getLatestAvailableCurrencyRates();

        List<BobaFettUser> users = bobaFettUsersRepository.withTransaction(tx -> {
            List<BobaFettUser> oldUsers = currencyDistributionUsersProvider.findUsersToDistribution(
                    tx,
                    hour,
                    minute
            );
            List<BobaFettUser> updatedUsers = oldUsers.stream()
                    .filter(
                            it -> !latestCurrencyRate.id()
                                    .equals(it.currencySubscriptionOptions().lastReceivedCurrencyRates().orElseNull())
                    )
                    .map(it -> {
                        CurrencySubscriptionOptions options = it.currencySubscriptionOptions().toBuilder()
                                .lastReceivedCurrencyRates(Opt.of(latestCurrencyRate.id()))
                                .build();
                        return it.toBuilder().currencySubscriptionOptions(options).build();
                    })
                    .toList();

            tx.save(updatedUsers);
            return oldUsers; // для доступа к старым currencySubscriptionOptions.lastReceivedCurrencyRates
        });

        if (users.isEmpty())
            return;

        List<ObjectId> oldUsersRates = users.stream()
                .map(it -> it.currencySubscriptionOptions().lastReceivedCurrencyRates())
                .filter(Opt::isPresent)
                .map(Opt::get)
                .distinct()
                .toList();
        Map<ObjectId, CurrencyRate> rates = currencyRatesRepository.findAllByIdsAsMap(oldUsersRates, CurrencyRate::id);

        for (BobaFettUser user : users) {
            String message = currencyMessageGenerator.getCurrencyMessageFor(
                    user,
                    latestCurrencyRate,
                    user.currencySubscriptionOptions().lastReceivedCurrencyRates()
                            .flatMapO(id -> Opt.ofNullable(rates.get(id)))
            );
            feedbackExecutor.sendMessage(
                    user.chatId(),
                    message
            );
            hintAboutChangingSelectedCurrencyService.executeHint(user, feedbackExecutor);
        }
    }
}
