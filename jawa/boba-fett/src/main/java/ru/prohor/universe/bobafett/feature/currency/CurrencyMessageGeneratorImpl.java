package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyMessageGeneratorImpl implements CurrencyMessageGenerator {
    private final LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider;
    private final CurrencyMessageFormatter currencyMessageFormatter;
    private final List<Currency> currencyToSend;

    public CurrencyMessageGeneratorImpl(
            LatestAvailableCurrencyRatesProvider latestAvailableCurrencyRatesProvider,
            CurrencyMessageFormatter currencyMessageFormatter,
            @Value("${universe.boba-fett.currency.currency-to-send}") List<Currency> currencyToSend
    ) {
        this.latestAvailableCurrencyRatesProvider = latestAvailableCurrencyRatesProvider;
        this.currencyMessageFormatter = currencyMessageFormatter;
        this.currencyToSend = currencyToSend;
    }

    @Override
    public String getCurrencyMessage() {
        Map<Currency, Rate> ratesMap = latestAvailableCurrencyRatesProvider.getLatestAvailableCurrencyRates()
                .stream()
                .collect(Collectors.toMap(Rate::getCurrency, MonoFunction.identity()));
        List<Rate> rates = currencyToSend.stream().map(ratesMap::get).filter(Objects::nonNull).toList();
        return currencyMessageFormatter.format(rates);
    }
}
