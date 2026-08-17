package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import ru.prohor.universe.bobafett.data.dto.CurrencySubscriptionOptionsDto;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

@Builder(toBuilder = true)
public record CurrencySubscriptionOptions(
        DistributionTime dailyDistributionTime,
        boolean subscriptionIsActive
) implements MongoEntityPojo<CurrencySubscriptionOptionsDto> {
    @Override
    public CurrencySubscriptionOptionsDto toDto() {
        return new CurrencySubscriptionOptionsDto(
                dailyDistributionTime.toDto(),
                subscriptionIsActive
        );
    }

    public static CurrencySubscriptionOptions fromDto(CurrencySubscriptionOptionsDto currencySubscriptionOptions) {
        return new CurrencySubscriptionOptions(
                DistributionTime.fromDto(currencySubscriptionOptions.getDailyDistributionTime()),
                currencySubscriptionOptions.isSubscriptionIsActive()
        );
    }
}
