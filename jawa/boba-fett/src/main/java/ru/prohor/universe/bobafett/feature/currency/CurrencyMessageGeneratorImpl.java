package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyMessageGeneratorImpl implements CurrencyMessageGenerator {
    private final LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider;
    private final CurrencyMessageFormatter currencyMessageFormatter;
    private final List<Currency> defaultCurrencyToSend;

    public CurrencyMessageGeneratorImpl(
            LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider,
            CurrencyMessageFormatter currencyMessageFormatter,
            @Value("${universe.boba-fett.currency.default-currency-to-send}") List<Currency> defaultCurrencyToSend
    ) {
        this.latestAvailableCurrencyRatesProvider = latestAvailableCurrencyRatesProvider;
        this.currencyMessageFormatter = currencyMessageFormatter;
        this.defaultCurrencyToSend = defaultCurrencyToSend;
    }

    @Override
    public String getCurrencyMessageFor(BobaFettUser user) {
        Map<Currency, Rate> ratesMap = latestAvailableCurrencyRatesProvider.getLatestAvailableCurrencyRates()
                .stream()
                .collect(Collectors.toMap(Rate::getCurrency, MonoFunction.identity()));
        List<Currency> currenciesToSend = user.currencySubscriptionOptions()
                .map(CurrencySubscriptionOptions::selectedCurrencies)
                .flatMapO(o -> o)
                .orElse(defaultCurrencyToSend);
        List<Rate> rates = currenciesToSend.stream().map(ratesMap::get).filter(Objects::nonNull).toList();
        return currencyMessageFormatter.format(rates);
    }
}
