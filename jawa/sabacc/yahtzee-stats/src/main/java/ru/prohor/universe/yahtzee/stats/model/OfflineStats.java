package ru.prohor.universe.yahtzee.stats.model;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
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
        @Id ObjectId id,
        @Property("generated_at") Instant generatedAt,
        @Property("top_limit") int topLimit,
        @Property("games_total") int gamesTotal,
        @Property("individual_results_total") int individualResultsTotal,
        @Property("individual_results_with_scores_total") int individualResultsWithScoresTotal,
        @Property("overall_average") float overallAverage,
        @Property("games_count") List<GamesCountStats> gamesCount,
        List<AverageStats> average,
        List<BoundStats> maximum,
        List<BoundStats> minimum,
        @Property("personal_maximum") List<PersonalMaximumStats> personalMaximum,
        @Property("total_distribution") List<ScoresDistributionStats> totalDistribution,
        @Property("simple_distribution") List<ScoresDistributionStats> simpleDistribution,
        @Property("most_frequent_total_scores") List<MostFrequentTotalScoresStats> mostFrequentTotalScores,
        @Property("missing_values") List<Integer> missingValues,
        @Property("simple_dice_average") List<FloatCombinationStats> simpleDiceAverage,
        @Property("simple_dice_distribution") List<SimpleDiceDistributionStats> simpleDiceDistribution,
        @Property("variable_combination_average") List<FloatCombinationStats> variableCombinationAverage,
        @Property("complex_combination_success_percent") List<FloatCombinationStats> complexCombinationSuccessPercent
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
