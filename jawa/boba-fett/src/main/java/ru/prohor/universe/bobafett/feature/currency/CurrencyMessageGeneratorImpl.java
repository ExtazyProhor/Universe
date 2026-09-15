package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CurrencyMessageGeneratorImpl implements CurrencyMessageGenerator {
    private final CurrencyMessageFormatter currencyMessageFormatter;

    public CurrencyMessageGeneratorImpl(CurrencyMessageFormatter currencyMessageFormatter) {
        this.currencyMessageFormatter = currencyMessageFormatter;
    }

    @Override
    public String getCurrencyMessageFor(
            BobaFettUser user,
            CurrencyRate currentCurrencyRate,
            Opt<CurrencyRate> lastCurrencyRate
    ) {
        Map<Currency, Rate> ratesMap = currentCurrencyRate.rates().stream()
                .collect(Collectors.toMap(Rate::getCurrency, MonoFunction.identity()));
        Map<Currency, Rate> lastTaresMap = lastCurrencyRate.map(CurrencyRate::rates).orElse(List.of()).stream()
                .collect(Collectors.toMap(Rate::getCurrency, MonoFunction.identity()));
        List<Currency> currenciesToSend = user.currencySubscriptionOptions().selectedCurrencies();
        List<Rate> currentRates = currenciesToSend.stream().map(ratesMap::get).filter(Objects::nonNull).toList();
        Opt<List<Rate>> lastRates = Opt.when(
                lastCurrencyRate.isPresent(),
                () -> currenciesToSend.stream().map(lastTaresMap::get).filter(Objects::nonNull).toList()
        );
        return currencyMessageFormatter.format(currentRates, lastRates);
    }
}
