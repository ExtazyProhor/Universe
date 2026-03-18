package ru.prohor.universe.yahtzee.stats;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple3;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.Yahtzee;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Score;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Team;
import ru.prohor.universe.yahtzee.stats.model.Stats;
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

    private final MongoRepository<Game> gameRepository;
    private final MongoRepository<Stats> statsRepository;

    public LocalStatisticsCalculationService(
            MongoRepository<Game> gameRepository,
            MongoRepository<Stats> statsRepository
    ) {
        this.gameRepository = gameRepository;
        this.statsRepository = statsRepository;
    }

    @Override
    public Opt<Stats> calculateAndGet() {
        List<Game> games = gameRepository.findAll().stream().filter(Game::trusted).toList();
        if (games.isEmpty())
            return Opt.empty();
        List<FlattenedGame> allTeamsWithGames = games.stream()
                .flatMap(game -> game.getTeams().stream().map(team -> new FlattenedGame(game, team)))
                .toList();
        Map<ObjectId, Long> gamesCount = allTeamsWithGames.stream()
                .flatMap(team -> team.team.players().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Team<Score>> teams = allTeamsWithGames.stream()
                .map(FlattenedGame::team)
                .filter(team -> team.scores().isPresent())
                .toList();
        List<Score> scores = teams.stream()
                .flatMap(team -> team.scores().get().stream())
                .toList();
        Stats stats = new Stats(
                ObjectId.get(),
                Instant.now(),
                TOP,
                games.size(),
                allTeamsWithGames.size(),
                teams.size(),
                (float) allTeamsWithGames.stream().mapToInt(game -> game.team.total()).average().orElse(0),
                calculateGamesCount(gamesCount),
                calculateAverage(allTeamsWithGames, gamesCount),
                calculateMaximum(allTeamsWithGames),
                calculateMinimum(allTeamsWithGames),
                calculatePersonalMaximum(allTeamsWithGames),
                calculateTotalDistribution(allTeamsWithGames),
                calculateSimpleDistribution(teams),
                calculateMostFrequentTotalScores(allTeamsWithGames),
                calculateMissingValues(allTeamsWithGames),
                calculateSimpleDiceAverage(scores),
                calculateSimpleDiceDistribution(scores),
                calculateVariableCombinationAverage(scores),
                calculateComplexCombinationSuccessPercent(scores)
        );
        statsRepository.save(stats);
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
                .map(FlattenedGame::team)
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
            game -> game.team.total()
    );

    private static final Comparator<FlattenedGame> COMPARATOR_BY_NOVELTY = Comparator
            .<FlattenedGame, Instant>comparing(game -> game.game().date())
            .reversed();

    private static final Comparator<FlattenedGame> COMPARATOR_BY_PLAYER = Comparator.comparing(
            game -> game.team.players().hashCode()
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
                        game.team.players(),
                        game.team.total(),
                        game.game.date(),
                        game.game.id()
                ))
                .toList();
    }

    private List<BoundStats> calculateMinimum(List<FlattenedGame> teams) {
        return teams.stream()
                .sorted(MIN_COMPARATOR)
                .limit(TOP)
                .map(game -> new BoundStats(
                        game.team.players(),
                        game.team.total(),
                        game.game.date(),
                        game.game.id()
                ))
                .toList();
    }

    private List<PersonalMaximumStats> calculatePersonalMaximum(List<FlattenedGame> teams) {
        Comparator<Tuple3<ObjectId, Integer, Game>> comparator = Comparator
                .<Tuple3<ObjectId, Integer, Game>, Integer>comparing(Tuple3::get2)
                .reversed()
                .thenComparing(Tuple3::get1);
        return teams.stream()
                .filter(team -> team.team.players().size() == 1)
                .collect(Collectors.groupingBy(game -> game.team.players().getFirst(), Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    FlattenedGame top = entry.getValue()
                            .stream()
                            .max(COMPARATOR_BY_TOTAL.thenComparing(COMPARATOR_BY_NOVELTY))
                            .orElseThrow();
                    return new Tuple3<>(
                            entry.getKey(),
                            top.team.total(),
                            top.game
                    );
                })
                .sorted(comparator)
                .limit(TOP)
                .map(tuple -> new PersonalMaximumStats(
                        tuple.get1(),
                        tuple.get2(),
                        tuple.get3().date(),
                        tuple.get3().id()
                ))
                .toList();
    }

    private List<ScoresDistributionStats> calculateTotalDistribution(List<FlattenedGame> teams) {
        float count = teams.size();
        return teams.stream()
                .map(FlattenedGame::team)
                .collect(Collectors.groupingBy(Team<Score>::total, Collectors.toList()))
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

    private List<ScoresDistributionStats> calculateSimpleDistribution(List<Team<Score>> teams) {
        float count = teams.size();
        Function<Team<Score>, Integer> simpleSummator = team -> team.scores()
                .get()
                .stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .mapToInt(Score::value)
                .sum();
        return teams.stream()
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
                .map(game -> game.team.total())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(comparator)
                .limit(TOP)
                .map(entry -> new MostFrequentTotalScoresStats(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    private List<Integer> calculateMissingValues(List<FlattenedGame> teams) {
        Set<Integer> presentValues = teams.stream().map(game -> game.team.total()).collect(Collectors.toSet());
        int maximum = presentValues.stream().mapToInt(i -> i).max().orElse(0);
        int minimum = presentValues.stream().mapToInt(i -> i).min().orElse(0);
        return IntStream.range(minimum, maximum).filter(i -> !presentValues.contains(i)).boxed().toList();
    }

    private List<FloatCombinationStats> calculateSimpleDiceAverage(List<Score> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .collect(Collectors.groupingBy(Score::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(this::mapFloatCombinationStats)
                .toList();
    }

    private FloatCombinationStats mapFloatCombinationStats(Map.Entry<Combination, List<Score>> entry) {
        int denomination = Yahtzee.getSimpleCombinationDenomination(entry.getKey());
        return new FloatCombinationStats(
                entry.getKey(),
                (float) entry.getValue()
                        .stream()
                        .mapToInt(score -> score.value() / denomination)
                        .average()
                        .orElse(0)
        );
    }

    private List<SimpleDiceDistributionStats> calculateSimpleDiceDistribution(List<Score> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isSimple(score.combination()))
                .collect(Collectors.groupingBy(Score::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(this::mapSimpleDiceDistributionStats)
                .toList();
    }

    private SimpleDiceDistributionStats mapSimpleDiceDistributionStats(
            Map.Entry<Combination, List<Score>> entry
    ) {
        float count = entry.getValue().size();
        int denomination = Yahtzee.getSimpleCombinationDenomination(entry.getKey());
        return new SimpleDiceDistributionStats(
                entry.getKey(),
                entry.getValue()
                        .stream()
                        .collect(Collectors.groupingBy(Score::value, Collectors.counting()))
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

    private List<FloatCombinationStats> calculateVariableCombinationAverage(List<Score> scores) {
        return scores.stream()
                .filter(score -> Yahtzee.isVariable(score.combination()))
                .collect(Collectors.groupingBy(Score::combination, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> new FloatCombinationStats(
                        entry.getKey(),
                        (float) entry.getValue()
                                .stream()
                                .mapToInt(Score::value)
                                .average()
                                .orElse(0)
                ))
                .toList();
    }

    private List<FloatCombinationStats> calculateComplexCombinationSuccessPercent(List<Score> scores) {
        return scores.stream()
                .filter(score -> !Yahtzee.isSimple(score.combination()) && score.combination() != Combination.CHANCE)
                .collect(Collectors.groupingBy(Score::combination, Collectors.toList()))
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
            Game game,
            Team<Score> team
    ) {}
}
