package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.distribution.DistributionTask;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyRatesDistributor implements DistributionTask {
    private final LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider;
    private final CurrencyDistributionUsersProvider currencyDistributionUsersProvider;
    private final CurrencyMessageFormatter currencyMessageFormatter;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;
    private final List<Currency> currencyToSend;

    public CurrencyRatesDistributor(
            LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider,
            CurrencyDistributionUsersProvider currencyDistributionUsersProvider,
            CurrencyMessageFormatter currencyMessageFormatter,
            MongoRepository<BobaFettUser> bobaFettUsersRepository,
            @Value("${universe.boba-fett.currency.currency-to-send}") List<Currency> currencyToSend
    ) {
        this.latestAvailableCurrencyRatesProvider = latestAvailableCurrencyRatesProvider;
        this.currencyDistributionUsersProvider = currencyDistributionUsersProvider;
        this.currencyMessageFormatter = currencyMessageFormatter;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
        this.currencyToSend = currencyToSend;
    }

    @Override
    public void distribute(FeedbackExecutor feedbackExecutor, int hour, int minute) {
        List<BobaFettUser> users = currencyDistributionUsersProvider.findUsersToDistribution(
                bobaFettUsersRepository,
                hour,
                minute
        );
        if (users.isEmpty())
            return;

        Map<Currency, Rate> ratesMap = latestAvailableCurrencyRatesProvider.getLatestAvailableCurrencyRates()
                .stream()
                .collect(Collectors.toMap(Rate::getCurrency, MonoFunction.identity()));
        List<Rate> rates = currencyToSend.stream().map(ratesMap::get).filter(Objects::nonNull).toList();

        for (BobaFettUser user : users) {
            feedbackExecutor.sendMessage(user.chatId(), currencyMessageFormatter.format(rates));
        }
    }
}
