package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.pojo.CurrencySubscriptionOptions;
import ru.prohor.universe.bobafett.data.pojo.DistributionTime;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

@Service
public class CurrencyService {
    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;

    private final List<Currency> defaultCurrency;

    public CurrencyService(
            @Value("${universe.boba-fett.currency.default-currency-to-send}") List<Currency> defaultCurrency
    ) {
        this.defaultCurrency = defaultCurrency;
    }

    public CurrencySubscriptionOptions createOptions(
            Opt<DistributionTime> time,
            Opt<Boolean> subscriptionIsActive,
            Opt<List<Currency>> selectedCurrencies
    ) {
        return new CurrencySubscriptionOptions(
                time.orElseGet(() -> new DistributionTime(DEFAULT_HOUR, DEFAULT_MINUTE)),
                subscriptionIsActive.orElse(false),
                Opt.of(selectedCurrencies.orElse(defaultCurrency))
        );
    }
}
