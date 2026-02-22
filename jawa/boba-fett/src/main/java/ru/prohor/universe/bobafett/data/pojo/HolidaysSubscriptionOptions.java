package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import ru.prohor.universe.bobafett.data.dto.HolidaysSubscriptionOptionsDto;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

@Builder(toBuilder = true)
public record HolidaysSubscriptionOptions(
        DistributionTime dailyDistributionTime,
        int indentationOfDays,
        boolean subscriptionIsActive
) implements MongoEntityPojo<HolidaysSubscriptionOptionsDto> {
    @Override
    public HolidaysSubscriptionOptionsDto toDto() {
        return new HolidaysSubscriptionOptionsDto(
                dailyDistributionTime.toDto(),
                indentationOfDays,
                subscriptionIsActive
        );
    }

    public static HolidaysSubscriptionOptions fromDto(HolidaysSubscriptionOptionsDto holidaysSubscriptionOptions) {
        return new HolidaysSubscriptionOptions(
                DistributionTime.fromDto(holidaysSubscriptionOptions.getDailyDistributionTime()),
                holidaysSubscriptionOptions.getIndentationOfDays(),
                holidaysSubscriptionOptions.isSubscriptionIsActive()
        );
    }
}
