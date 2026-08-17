package ru.prohor.universe.bobafett.feature.currency;

import ru.prohor.universe.bobafett.data.dto.Rate;

import java.util.List;

public interface CurrencyMessageFormatter {
    String format(List<Rate> rates);
}
