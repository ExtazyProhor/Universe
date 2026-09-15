package ru.prohor.universe.bobafett.feature.currency;

import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;

public interface LatestAvailableCurrencyRatesProvider {
    CurrencyRate getLatestAvailableCurrencyRates();
}
