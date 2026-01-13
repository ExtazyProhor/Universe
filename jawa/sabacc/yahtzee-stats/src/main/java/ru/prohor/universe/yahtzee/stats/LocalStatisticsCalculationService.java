package ru.prohor.universe.yahtzee.stats;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple3;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.core.Combination;
import ru.prohor.universe.yahtzee.core.core.Yahtzee;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineScore;
import ru.prohor.universe.yahtzee.offline.data.inner.pojo.OfflineTeamScores;
import ru.prohor.universe.yahtzee.stats.model.OfflineStats;
import ru.prohor.universe.yahtzee.stats.model.inner.AverageStats;
import ru.prohor.universe.yahtzee.stats.model.inner.BoundStats;
import ru.prohor.universe.yahtzee.stats.model.inner.FloatCombinationStats;
import ru.prohor.universe.yahtzee.stats.model.inner.GamesCountStats;
import ru.prohor.universe.yahtzee.stats.model.inner.MostFrequentTotalScoresStats;
import ru.prohor.universe.yahtzee.stats.model.inner.PersonalMaximumStats;
import ru.prohor.universe.yahtzee.stats.model.inner.ScoresDistributionStats;
import ru.prohor.universe.yahtzee.stats.model.inner.SimpleDiceDistributionStats;
import ru.prohor.universe.yahtzee.stats.model.inner.SimpleDicePerCountDistribution;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LocalStatisticsCalculationService implements StatisticsCalculationService {
    private final static int TOP = 10;
    private final static int CALIBRATION_GAMES = 10;

    private final MongoRepository<OfflineGame> offlineGameRepository;
    private final MongoRepository<OfflineStats> offlineStatsRepository;

    public LocalStatisticsCalculationService(
            MongoRepository<OfflineGame> offlineGameRepository,
            MongoRepository<OfflineStats> offlineStatsRepository
    ) {
        this.offlineGameRepository = offlineGameRepository;
        this.offlineStatsRepository = offlineStatsRepository;
    }

    @Override
    public Opt<OfflineStats> calculateAndGet() {
        List<OfflineGame> games = offlineGameRepository.findAll().stream().filter(OfflineGame::trusted).toList();
        if (games.isEmpty())
            return Opt.empty();
        List<FlattenedGame> teams = games.stream()
                .flatMap(game -> game.teams().stream().map(team -> new FlattenedGame(game, team)))
                .toList();
        Map<ObjectId, Long> gamesCount = teams.stream()
                .flatMap(team -> team.teamScores.players().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<OfflineTeamScores> teamScores = teams.stream()
                .map(FlattenedGame::teamScores)
                .filter(team -> team.scores().isPresent())
                .toList();
        List<OfflineScore> scores = teamScores.stream()
                .flatMap(team -> team.scores().get().stream())
                .toList();
        OfflineStats stats = new OfflineStats(
                ObjectId.get(),
                Instant.now(),
                TOP,
                games.size(),
                teams.size(),
                teamScores.size(),
                (float) teams.stream().mapToInt(game -> game.teamScores.total()).average().orElse(0),
                calculateGamesCount(gamesCount),
                calculateAverage(teams, gamesCount),
                calculateMaximum(teams),
                calculateMinimum(teams),
                calculatePersonalMaximum(teams),
                calculateTotalDistribution(teams),
                calculateSimpleDistribution(teamScores),
                calculateMostFrequentTotalScores(teams),
                calculateMissingValues(teams),
                calculateSimpleDiceAverage(scores),
                calculateSimpleDiceDistribution(scores),
                calculateVariableCombinationAverage(scores),
                calculateComplexCombinationSuccessPercent(scores)
        );
        offlineStatsRepository.save(stats);
        return Opt.of(stats);
    }

    private List<GamesCountStats> calculateGamesCount(Map<ObjectId, Long> gamesCount) {
        Comparator<Map.Entry<ObjectId, Long>> comparator = Map.Entry.<ObjectId, Long>comparingByValue()
                .reversed()
                .thenComparing(Map.Entry.comparingByKey());
        return gamesCount.entrySet()
                .stream()
                .sorted(comparator)
                .limit(TOP)
                .map(entry -> new GamesCountStats(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    private List<AverageStats> calculateAverage(List<FlattenedGame> teams, Map<ObjectId, Long> gamesCount) {
        return teams.stream()
                .map(FlattenedGame::teamScores)
                .flatMap(team -> team.players().stream().map(player -> new Tuple2<>(player, team.total())))
                .filter(tuple -> gamesCount.get(tuple.get1()) >= CALIBRATION_GAMES)
                .collect(Collectors.groupingBy(Tuple2::get1, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new AverageStats(
                        entry.getKey(),
                        (float) entry.getValue().stream().mapToInt(Tuple2::get2).average().orElse(0)
                ))
                .sorted(Comparator.comparing(AverageStats::getValue).reversed())
                .limit(TOP)
                .toList();
    }

    private static final Comparator<FlattenedGame> COMPARATOR_BY_TOTAL = Comparator.comparing(
            game -> game.teamScores().total()
    );

    private static final Comparator<FlattenedGame> COMPARATOR_BY_NOVELTY = Comparator
            .<FlattenedGame, Instant>comparing(game -> game.game().date())
            .reversed();

    private static final Comparator<FlattenedGame> COMPARATOR_BY_PLAYER = Comparator.comparing(
            game -> game.teamScores.players().hashCode()
    );

    private static final Comparator<FlattenedGame> MAX_COMPARATOR = COMPARATOR_BY_TOTAL.reversed()
            .thenComparing(COMPARATOR_BY_NOVELTY)
            .thenComparing(COMPARATOR_BY_PLAYER);

    private static final Comparator<FlattenedGame> MIN_COMPARATOR = COMPARATOR_BY_TOTAL
            .thenComparing(COMPARATOR_BY_NOVELTY)
            .thenComparing(COMPARATOR_BY_PLAYER);

    private List<BoundStats> calculateMaximum(List<FlattenedGame> teams) {
        return teams.stream()
                .sorted(MAX_COMPARATOR)
                .limit(TOP)
                .map(game -> new BoundStats(
                        game.teamScores.players(),
                        game.teamScores.total(),
                        game.game.date()
                ))
                .toList();
    }

    private List<BoundStats> calculateMinimum(List<FlattenedGame> teams) {
        return teams.stream()
                .sorted(MIN_COMPARATOR)
                .limit(TOP)
                .map(game -> new BoundStats(
                        game.teamScores.players(),
                        game.teamScores.total(),
                        game.game.date()
                ))
                .toList();
    }

    private List<PersonalMaximumStats> calculatePersonalMaximum(List<FlattenedGame> teams) {
        Comparator<Tuple3<ObjectId, Integer, OfflineGame>> comparator = Comparator
                .<Tuple3<ObjectId, Integer, OfflineGame>, Integer>comparing(Tuple3::get2)
                .reversed()
                .thenComparing(Tuple3::get1);
        return teams.stream()
                .filter(team -> team.teamScores.players().size() == 1)
                .collect(Collectors.groupingBy(game -> game.teamScores.players().getFirst(), Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    FlattenedGame top = entry.getValue()
                            .stream()
                            .max(COMPARATOR_BY_TOTAL.thenComparing(COMPARATOR_BY_NOVELTY))
                            .orElseThrow();
                    return new Tuple3<>(
                            entry.getKey(),
                            top.teamScores.total(),
                            top.game
                    );
                })
                .sorted(comparator)
                .limit(TOP)
                .map(tuple -> new PersonalMaximumStats(tuple.get1(), tuple.get2(), tuple.get3().date()))
                .toList();
    }

    private List<ScoresDistributionStats> calculateTotalDistribution(List<FlattenedGame> teams) {
        float count = teams.size();
        return teams.stream()
                .map(FlattenedGame::teamScores)
                .collect(Collectors.groupingBy(OfflineTeamScores::total, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ScoresDistributionStats(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().size() * 100f / count
                ))
                .sorted(Comparator.comparing(ScoresDistributionStats::getValue))
                .toList();
    }

    private List<ScoresDistributionStats> calculateSimpleDistribution(List<OfflineTeamScores> teamScores) {
        float count = teamScores.size();
        Function<OfflineTeamScores, Integer> simpleSummator = team -> team.scores()
                .get()
                .stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .mapToInt(OfflineScore::value)
                .sum();
        return teamScores.stream()
                .collect(Collectors.groupingBy(simpleSummator, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new ScoresDistributionStats(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().size() / count * 100
                ))
                .sorted(Comparator.comparing(ScoresDistributionStats::getValue))
                .toList();
    }

    private List<MostFrequentTotalScoresStats> calculateMostFrequentTotalScores(List<FlattenedGame> teams) {
        Comparator<Map.Entry<Integer, Long>> comparator = Map.Entry.<Integer, Long>comparingByValue()
                .reversed()
                .thenComparing(Map.Entry.comparingByKey());
        return teams.stream()
                .map(game -> game.teamScores.total())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(comparator)
                .limit(TOP)
                .map(entry -> new MostFrequentTotalScoresStats(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    private List<Integer> calculateMissingValues(List<FlattenedGame> teams) {
        Set<Integer> presentValues = teams.stream().map(game -> game.teamScores.total()).collect(Collectors.toSet());
        int maximum = presentValues.stream().mapToInt(i -> i).max().orElse(0);
        int minimum = presentValues.stream().mapToInt(i -> i).min().orElse(0);
        return IntStream.range(minimum, maximum).filter(i -> !presentValues.contains(i)).boxed().toList();
    }

    private List<FloatCombinationStats> calculateSimpleDiceAverage(List<OfflineScore> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .collect(Collectors.groupingBy(OfflineScore::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    int denomination = Yahtzee.getSimpleCombinationDenomination(entry.getKey());
                    return new FloatCombinationStats(
                            entry.getKey(),
                            (float) entry.getValue()
                                    .stream()
                                    .mapToInt(score -> score.value() / denomination)
                                    .average()
                                    .orElse(0)
                    );
                })
                .toList();
    }

    private List<SimpleDiceDistributionStats> calculateSimpleDiceDistribution(List<OfflineScore> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .collect(Collectors.groupingBy(OfflineScore::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(this::mapSimpleDiceDistributionStats)
                .toList();
    }

    private SimpleDiceDistributionStats mapSimpleDiceDistributionStats(
            Map.Entry<Combination, List<OfflineScore>> entry
    ) {
        float count = entry.getValue().size();
        int denomination = Yahtzee.getSimpleCombinationDenomination(entry.getKey());
        return new SimpleDiceDistributionStats(
                entry.getKey(),
                entry.getValue()
                        .stream()
                        .collect(Collectors.groupingBy(OfflineScore::value, Collectors.counting()))
                        .entrySet()
                        .stream()
                        .map(scoreEntry -> mapSimpleDicePerCountDistribution(
                                scoreEntry,
                                denomination,
                                count
                        ))
                        .sorted(Comparator.comparing(SimpleDicePerCountDistribution::getDice))
                        .toList()
        );
    }

    private SimpleDicePerCountDistribution mapSimpleDicePerCountDistribution(
            Map.Entry<Integer, Long> scoreEntry,
            int denomination,
            float count
    ) {
        return new SimpleDicePerCountDistribution(
                scoreEntry.getKey() / denomination,
                scoreEntry.getValue().intValue(),
                scoreEntry.getValue().intValue() * 100f / count
        );
    }

    private List<FloatCombinationStats> calculateVariableCombinationAverage(List<OfflineScore> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isVariable(score.combination()))
                .collect(Collectors.groupingBy(OfflineScore::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new FloatCombinationStats(
                        entry.getKey(),
                        (float) entry.getValue()
                                .stream()
                                .mapToInt(OfflineScore::value)
                                .average()
                                .orElse(0)
                ))
                .toList();
    }

    private List<FloatCombinationStats> calculateComplexCombinationSuccessPercent(List<OfflineScore> scores) {
        return scores.stream()
                .filter(score -> !Yahtzee.isSimple(score.combination()) && score.combination() != Combination.CHANCE)
                .collect(Collectors.groupingBy(OfflineScore::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new FloatCombinationStats(
                        entry.getKey(),
                        100f * (float) entry.getValue()
                                .stream()
                                .mapToInt(score -> score.value() == 0 ? 0 : 1)
                                .average()
                                .orElse(0)
                ))
                .toList();
    }

    private record FlattenedGame(
            OfflineGame game,
            OfflineTeamScores teamScores
    ) {}
}
