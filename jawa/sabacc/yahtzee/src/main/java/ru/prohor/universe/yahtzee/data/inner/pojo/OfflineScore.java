package ru.prohor.universe.yahtzee.data.inner.pojo;

import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.OfflineScoreDto;

public record OfflineScore(
        String combination,
        int value
) implements MongoEntityPojo<OfflineScoreDto> {
    @Override
    public OfflineScoreDto toDto() {
        return new OfflineScoreDto(combination, value);
    }

    public static OfflineScore fromDto(OfflineScoreDto score) {
        return new OfflineScore(score.getCombination(), score.getValue());
    }
}
