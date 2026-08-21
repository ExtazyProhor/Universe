package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.prohor.universe.bobafett.data.Currency;

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
    private List<Currency> selectedCurrencies;
    @Property("hint_about_changing_selected_currency_disabled")
    private Boolean hintAboutChangingSelectedCurrencyDisabled;
}
