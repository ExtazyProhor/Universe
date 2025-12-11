package ru.prohor.universe.bobafett.data.pojo;

import ru.prohor.universe.bobafett.data.dto.DistributionTimeDto;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

public record DistributionTime(int hour, int minute) implements MongoEntityPojo<DistributionTimeDto> {
    @Override
    public DistributionTimeDto toDto() {
        return new DistributionTimeDto(hour, minute);
    }

    public static DistributionTime fromDto(DistributionTimeDto distributionTime) {
        return new DistributionTime(
                distributionTime.getHour(),
                distributionTime.getMinute()
        );
    }
}
