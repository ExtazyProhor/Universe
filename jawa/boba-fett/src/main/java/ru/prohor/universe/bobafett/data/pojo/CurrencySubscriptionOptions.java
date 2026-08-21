package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.CurrencySubscriptionOptionsDto;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

import java.util.List;

@Builder(toBuilder = true)
public record CurrencySubscriptionOptions(
        DistributionTime dailyDistributionTime,
        boolean subscriptionIsActive,
        List<Currency> selectedCurrencies,
        boolean hintAboutChangingSelectedCurrencyDisabled
) implements MongoEntityPojo<CurrencySubscriptionOptionsDto> {
    @Override
    public CurrencySubscriptionOptionsDto toDto() {
        return new CurrencySubscriptionOptionsDto(
                dailyDistributionTime.toDto(),
                subscriptionIsActive,
                selectedCurrencies,
                hintAboutChangingSelectedCurrencyDisabled
        );
    }

    public static CurrencySubscriptionOptions fromDto(CurrencySubscriptionOptionsDto options) {
        return new CurrencySubscriptionOptions(
                DistributionTime.fromDto(options.getDailyDistributionTime()),
                options.isSubscriptionIsActive(),
                options.getSelectedCurrencies(),
                Opt.ofNullable(options.getHintAboutChangingSelectedCurrencyDisabled()).orElse(false)
        );
    }
}
