package ru.prohor.universe.yahtzee.stats.model;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;
import ru.prohor.universe.yahtzee.stats.model.inner.AverageStats;
import ru.prohor.universe.yahtzee.stats.model.inner.BoundStats;
import ru.prohor.universe.yahtzee.stats.model.inner.FloatCombinationStats;
import ru.prohor.universe.yahtzee.stats.model.inner.GamesCountStats;
import ru.prohor.universe.yahtzee.stats.model.inner.MostFrequentTotalScoresStats;
import ru.prohor.universe.yahtzee.stats.model.inner.PersonalMaximumStats;
import ru.prohor.universe.yahtzee.stats.model.inner.ScoresDistributionStats;
import ru.prohor.universe.yahtzee.stats.model.inner.SimpleDiceDistributionStats;

import java.time.Instant;
import java.util.List;

public record OfflineStats(
        ObjectId id,
        Instant generatedAt,
        int topLimit,
        int gamesTotal,
        int individualResultsTotal,
        int individualResultsWithScoresTotal,
        float overallAverage,
        List<GamesCountStats> gamesCount,
        List<AverageStats> average,
        List<BoundStats> maximum,
        List<BoundStats> minimum,
        List<PersonalMaximumStats> personalMaximum,
        List<ScoresDistributionStats> totalDistribution,
        List<ScoresDistributionStats> simpleDistribution,
        List<MostFrequentTotalScoresStats> mostFrequentTotalScores,
        List<Integer> missingValues,
        List<FloatCombinationStats> simpleDiceAverage,
        List<SimpleDiceDistributionStats> simpleDiceDistribution,
        List<FloatCombinationStats> variableCombinationAverage,
        List<FloatCombinationStats> complexCombinationSuccessPercent
) implements MongoEntityPojo<OfflineStatsDto> {
    @Override
    public OfflineStatsDto toDto() {
        return new OfflineStatsDto(
                id,
                generatedAt,
                topLimit,
                gamesTotal,
                individualResultsTotal,
                individualResultsWithScoresTotal,
                overallAverage,
                gamesCount,
                average,
                maximum,
                minimum,
                personalMaximum,
                totalDistribution,
                simpleDistribution,
                mostFrequentTotalScores,
                missingValues,
                simpleDiceAverage,
                simpleDiceDistribution,
                variableCombinationAverage,
                complexCombinationSuccessPercent
        );
    }

    public static OfflineStats fromDto(OfflineStatsDto stats) {
        return new OfflineStats(
                stats.getId(),
                stats.getGeneratedAt(),
                stats.getTopLimit(),
                stats.getGamesTotal(),
                stats.getIndividualResultsTotal(),
                stats.getIndividualResultsWithScoresTotal(),
                stats.getOverallAverage(),
                stats.getGamesCount(),
                stats.getAverage(),
                stats.getMaximum(),
                stats.getMinimum(),
                stats.getPersonalMaximum(),
                stats.getTotalDistribution(),
                stats.getSimpleDistribution(),
                stats.getMostFrequentTotalScores(),
                stats.getMissingValues(),
                stats.getSimpleDiceAverage(),
                stats.getSimpleDiceDistribution(),
                stats.getVariableCombinationAverage(),
                stats.getComplexCombinationSuccessPercent()
        );
    }
}
