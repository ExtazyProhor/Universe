package ru.prohor.universe.bobafett.feature.currency;

import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;

public interface CurrencyMessageGenerator {
    String getCurrencyMessageFor(BobaFettUser user);
}
