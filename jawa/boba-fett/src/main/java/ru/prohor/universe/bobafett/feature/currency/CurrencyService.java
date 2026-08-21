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
    private final List<Currency> defaultCurrency;

    public CurrencyService(
            @Value("${universe.boba-fett.currency.default-currency-to-send}") List<Currency> defaultCurrency
    ) {
        this.defaultCurrency = defaultCurrency;
    }

    public CurrencySubscriptionOptions createOptions(
            DistributionTime time,
            boolean subscriptionIsActive,
            Opt<List<Currency>> selectedCurrencies
    ) {
        return new CurrencySubscriptionOptions(
                time,
                subscriptionIsActive,
                Opt.of(selectedCurrencies.orElse(defaultCurrency))
        );
    }
}
