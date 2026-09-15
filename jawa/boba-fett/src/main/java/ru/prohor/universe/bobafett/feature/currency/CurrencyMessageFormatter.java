package ru.prohor.universe.bobafett.feature.currency;

import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

public interface CurrencyMessageFormatter {
    String format(List<Rate> currentRates, Opt<List<Rate>> lastRates);
}
