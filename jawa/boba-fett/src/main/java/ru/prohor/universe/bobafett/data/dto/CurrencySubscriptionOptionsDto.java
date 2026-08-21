package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CurrencySubscriptionOptionsDto {
    @Property("daily_distribution_time")
    private DistributionTimeDto dailyDistributionTime;
    @Property("subscription_is_active")
    private boolean subscriptionIsActive;
    @Property("selected_currencies")
    private Opt<List<Currency>> selectedCurrencies;
}
