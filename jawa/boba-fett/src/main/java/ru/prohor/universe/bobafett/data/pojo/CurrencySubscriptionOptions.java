package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.CurrencySubscriptionOptionsDto;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

import java.util.List;

@Builder(toBuilder = true)
public record CurrencySubscriptionOptions(
        DistributionTime dailyDistributionTime,
        boolean subscriptionIsActive,
        List<Currency> selectedCurrencies
) implements MongoEntityPojo<CurrencySubscriptionOptionsDto> {
    @Override
    public CurrencySubscriptionOptionsDto toDto() {
        return new CurrencySubscriptionOptionsDto(
                dailyDistributionTime.toDto(),
                subscriptionIsActive,
                selectedCurrencies
        );
    }

    public static CurrencySubscriptionOptions fromDto(CurrencySubscriptionOptionsDto currencySubscriptionOptions) {
        return new CurrencySubscriptionOptions(
                DistributionTime.fromDto(currencySubscriptionOptions.getDailyDistributionTime()),
                currencySubscriptionOptions.isSubscriptionIsActive(),
                currencySubscriptionOptions.getSelectedCurrencies()
        );
    }
}
