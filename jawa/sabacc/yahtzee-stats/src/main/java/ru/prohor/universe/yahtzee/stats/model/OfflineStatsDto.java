package ru.prohor.universe.yahtzee.stats.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
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

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("offline_stats")
public class OfflineStatsDto {
    @Id
    private ObjectId id;
    @Property("generated_at")
    private Instant generatedAt;
    @Property("top_limit")
    private int topLimit;
    @Property("games_total")
    private int gamesTotal;
    @Property("individual_results_total")
    private int individualResultsTotal;
    @Property("individual_results_with_scores_total")
    private int individualResultsWithScoresTotal;
    @Property("overall_average")
    private float overallAverage;

    @Property("games_count")
    private List<GamesCountStats> gamesCount;
    private List<AverageStats> average;
    private List<BoundStats> maximum;
    private List<BoundStats> minimum;
    @Property("personal_maximum")
    private List<PersonalMaximumStats> personalMaximum;

    @Property("total_distribution")
    private List<ScoresDistributionStats> totalDistribution;
    @Property("simple_distribution")
    private List<ScoresDistributionStats> simpleDistribution;
    @Property("most_frequent_total_scores")
    private List<MostFrequentTotalScoresStats> mostFrequentTotalScores;
    @Property("missing_values")
    private List<Integer> missingValues;
    @Property("simple_dice_average")
    private List<FloatCombinationStats> simpleDiceAverage;
    @Property("simple_dice_distribution")
    private List<SimpleDiceDistributionStats> simpleDiceDistribution;
    @Property("variable_combination_average")
    private List<FloatCombinationStats> variableCombinationAverage;
    @Property("complex_combination_success_percent")
    private List<FloatCombinationStats> complexCombinationSuccessPercent;
}
