package ru.prohor.universe.bobafett.feature.currency;

import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public interface CurrencyMessageGenerator {
    String getCurrencyMessageFor(
            BobaFettUser user,
            CurrencyRate currentCurrencyRate,
            Opt<CurrencyRate> lastCurrencyRate
    );
}
