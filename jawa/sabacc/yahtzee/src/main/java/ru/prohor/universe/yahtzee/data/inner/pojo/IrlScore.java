package ru.prohor.universe.yahtzee.data.inner.pojo;

import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.data.inner.dto.IrlScoreDto;

public record IrlScore(
        String combination,
        int value
) implements MongoEntityPojo<IrlScoreDto> {
    @Override
    public IrlScoreDto toDto() {
        return new IrlScoreDto(combination, value);
    }

    public static IrlScore fromDto(IrlScoreDto score) {
        return new IrlScore(score.getCombination(), score.getValue());
    }
}
