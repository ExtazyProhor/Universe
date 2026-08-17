package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CurrencySubscriptionOptionsDto {
    @Property("daily_distribution_time")
    private DistributionTimeDto dailyDistributionTime;
    @Property("subscription_is_active")
    private boolean subscriptionIsActive;
}
