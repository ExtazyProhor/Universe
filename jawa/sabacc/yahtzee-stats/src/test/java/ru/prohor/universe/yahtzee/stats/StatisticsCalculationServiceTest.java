package ru.prohor.universe.yahtzee.stats;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple4;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoInMemoryRepository;
import ru.prohor.universe.yahtzee.core.data.Combination;
import ru.prohor.universe.yahtzee.core.data.GameType;
import ru.prohor.universe.yahtzee.core.data.TactileGameSource;
import ru.prohor.universe.yahtzee.core.data.dto.game.TactileGamePropertiesDto;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.game.TactileGame;
import ru.prohor.universe.yahtzee.core.data.pojo.game.TactileScore;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatisticsCalculationServiceTest {
    private static final ObjectId ALICE = ObjectId.get();
    private static final ObjectId BOB = ObjectId.get();
    private static final ObjectId CHARLIE = ObjectId.get();
    private static final ObjectId DAVID = ObjectId.get();
    private static final ObjectId EVE = ObjectId.get();
    private static final ObjectId FRANK = ObjectId.get();

    private static final ObjectId GAME_12_27 = ObjectId.get();
    private static final ObjectId GAME_12_28 = ObjectId.get();
    private static final ObjectId GAME_12_29 = ObjectId.get();
    private static final ObjectId GAME_12_30 = ObjectId.get();
    private static final ObjectId GAME_12_31 = ObjectId.get();
    private static final ObjectId GAME_01_01 = ObjectId.get();
    private static final ObjectId GAME_01_02 = ObjectId.get();
    private static final ObjectId GAME_01_03 = ObjectId.get();
    private static final ObjectId GAME_01_04 = ObjectId.get();
    private static final ObjectId GAME_01_05 = ObjectId.get();
    private static final ObjectId GAME_01_06 = ObjectId.get();
    private static final ObjectId GAME_01_07 = ObjectId.get();
    private static final ObjectId GAME_01_08 = ObjectId.get();
    private static final ObjectId GAME_01_09 = ObjectId.get();
    private static final ObjectId GAME_01_10 = ObjectId.get();
    private static final ObjectId GAME_01_11 = ObjectId.get();
    private static final ObjectId GAME_01_12 = ObjectId.get();
    private static final ObjectId GAME_01_13 = ObjectId.get();
    private static final ObjectId GAME_01_14 = ObjectId.get();

    private final MongoRepository<Game> gamesRepository = new MongoInMemoryRepository<>(Game::id, Game.class);
    private final StatisticsCalculationService statisticsCalculationService = new LocalStatisticsCalculationService(
            gamesRepository,
            new MongoInMemoryRepository<>(Stats::id, Stats.class)
    );

    public StatisticsCalculationServiceTest() {
        gamesRepository.save(createGames());
    }

    private Game createObsidianTableTactileGame(
            ObjectId gameId,
            String date,
            ObjectId initiator,
            List<Tuple2<List<ObjectId>, Integer>> teams
    ) {
        return new TactileGame(
                gameId,
                teams.stream().flatMap(tuple -> tuple.get1().stream()).toList(),
                ofDate(date),
                initiator,
                teams.stream().map(
                        tuple -> new Team<TactileScore>(
                                tuple.get1(),
                                Opt.empty(),
                                tuple.get2(),
                                Opt.empty(),
                                Opt.empty(),
                                Opt.empty()
                        )
                ).toList(),
                true,
                Opt.empty(),
                GameType.TACTILE_OFFLINE,
                new TactileGamePropertiesDto(TactileGameSource.OBSIDIAN_TABLE)
        );
    }

    private Game createScoresTactileGame(
            ObjectId gameId,
            String date,
            ObjectId initiator,
            List<Tuple4<List<ObjectId>, List<Tuple2<Combination, Integer>>, Integer, Boolean>> teams
    ) {
        return new TactileGame(
                gameId,
                teams.stream().flatMap(tuple -> tuple.get1().stream()).toList(),
                ofDate(date),
                initiator,
                teams.stream().map(
                        tuple -> new Team<>(
                                tuple.get1(),
                                Opt.of(tuple.get2().stream().map(t2 -> new TactileScore(
                                        t2.get1(),
                                        t2.get2(),
                                        Opt.empty(),
                                        Opt.empty()
                                )).toList()),
                                tuple.get3(),
                                Opt.of(tuple.get4()),
                                Opt.empty(),
                                Opt.empty()
                        )
                ).toList(),
                true,
                Opt.empty(),
                GameType.TACTILE_OFFLINE,
                new TactileGamePropertiesDto(TactileGameSource.SCREENSHOT)
        );
    }

    private Game createUntrustedGame() {
        ObjectId player1 = ObjectId.get();
        ObjectId player2 = ObjectId.get();
        return new TactileGame(
                ObjectId.get(),
                List.of(player1, player2),
                LocalDate.of(2026, 1, 5).atTime(12, 0).toInstant(ZoneOffset.UTC),
                player1,
                List.of(
                        new Team<>(
                                List.of(player1),
                                Opt.empty(),
                                400,
                                Opt.empty(),
                                Opt.empty(),
                                Opt.empty()
                        ),
                        new Team<>(
                                List.of(player2),
                                Opt.empty(),
                                160,
                                Opt.empty(),
                                Opt.empty(),
                                Opt.empty()
                        )
                ),
                false,
                Opt.empty(),
                GameType.TACTILE_OFFLINE,
                new TactileGamePropertiesDto(TactileGameSource.OBSIDIAN_TABLE)
        );
    }

    private Game createUntrustedGameWithScores() {
        ObjectId player1 = ObjectId.get();
        ObjectId player2 = ObjectId.get();
        return new TactileGame(
                ObjectId.get(),
                List.of(player1, player2),
                LocalDate.of(2026, 1, 2).atTime(15, 0).toInstant(ZoneOffset.UTC),
                player1,
                List.of(
                        new Team<>(
                                List.of(player1),
                                Opt.of(List.of(
                                        new TactileScore(Combination.UNITS, 2, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.THREE_OF_KIND, 25, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.SIXES, 18, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.PAIR, 24, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FULL_HOUSE, 25, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.TWOS, 8, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.THREES, 9, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.LOW_STRAIGHT, 30, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.YAHTZEE, 0, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.TWO_PAIRS, 26, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.CHANCE, 16, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FOURS, 12, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.HIGH_STRAIGHT, 40, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FOUR_OF_KIND, 0, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FIVES, 10, Opt.empty(), Opt.empty())
                                )),
                                245,
                                Opt.of(false),
                                Opt.empty(),
                                Opt.empty()
                        ),
                        new Team<>(
                                List.of(player2),
                                Opt.of(List.of(
                                        new TactileScore(Combination.SIXES, 24, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FULL_HOUSE, 25, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.YAHTZEE, 50, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.TWO_PAIRS, 28, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FOUR_OF_KIND, 24, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.THREES, 9, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.THREE_OF_KIND, 25, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.LOW_STRAIGHT, 30, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.UNITS, 2, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.TWOS, 6, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.HIGH_STRAIGHT, 40, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.PAIR, 27, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FOURS, 12, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.CHANCE, 29, Opt.empty(), Opt.empty()),
                                        new TactileScore(Combination.FIVES, 10, Opt.empty(), Opt.empty())
                                )),
                                376,
                                Opt.of(true),
                                Opt.empty(),
                                Opt.empty()
                        )
                ),
                false,
                Opt.empty(),
                GameType.TACTILE_OFFLINE,
                new TactileGamePropertiesDto(TactileGameSource.LEGACY_JSON)
        );
    }

    private Instant ofDate(String date) {
        return LocalDate.parse(date).atTime(0, 0).toInstant(ZoneOffset.UTC);
    }

    private List<Game> createGames() {
        return List.of(
                createObsidianTableTactileGame(
                        GAME_12_27,
                        "2025-12-27",
                        FRANK,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 305)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_12_28,
                        "2025-12-28",
                        CHARLIE,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 298)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_12_29,
                        "2025-12-29",
                        EVE,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 267)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_12_30,
                        "2025-12-30",
                        EVE,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 333)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_12_31,
                        "2025-12-31",
                        BOB,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 350)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_01_01,
                        "2026-01-01",
                        ALICE,
                        List.of(
                                new Tuple2<>(List.of(ALICE), 280)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_01_02,
                        "2026-01-02",
                        ALICE,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB), 270)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_01_03,
                        "2026-01-03",
                        ALICE,
                        List.of(
                                new Tuple2<>(List.of(ALICE), 320),
                                new Tuple2<>(List.of(BOB), 310)
                        )
                ),
                createObsidianTableTactileGame(
                        GAME_01_04,
                        "2026-01-04",
                        DAVID,
                        List.of(
                                new Tuple2<>(List.of(ALICE, BOB, CHARLIE), 305),
                                new Tuple2<>(List.of(DAVID, EVE, FRANK), 230)
                        )
                ),
                createScoresTactileGame(
                        GAME_01_05,
                        "2026-01-05",
                        ALICE,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE),
                                        List.of(
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.TWOS, 6),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.FOURS, 16),
                                                new Tuple2<>(Combination.YAHTZEE, 50),
                                                new Tuple2<>(Combination.TWO_PAIRS, 28),
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.PAIR, 25),
                                                new Tuple2<>(Combination.CHANCE, 22),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 26),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.FIVES, 25),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 28),
                                                new Tuple2<>(Combination.SIXES, 24)
                                        ),
                                        390,
                                        true
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_06,
                        "2026-01-06",
                        BOB,
                        List.of(
                                new Tuple4<>(
                                        List.of(BOB),
                                        List.of(
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 26),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.TWO_PAIRS, 28),
                                                new Tuple2<>(Combination.YAHTZEE, 50),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.TWOS, 6),
                                                new Tuple2<>(Combination.CHANCE, 27),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.PAIR, 25),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 0),
                                                new Tuple2<>(Combination.FOURS, 8)
                                        ),
                                        308,
                                        false
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_07,
                        "2026-01-07",
                        CHARLIE,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE, BOB, CHARLIE),
                                        List.of(
                                                new Tuple2<>(Combination.UNITS, 2),
                                                new Tuple2<>(Combination.TWOS, 6),
                                                new Tuple2<>(Combination.THREES, 6),
                                                new Tuple2<>(Combination.FOURS, 12),
                                                new Tuple2<>(Combination.FIVES, 20),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.PAIR, 26),
                                                new Tuple2<>(Combination.TWO_PAIRS, 27),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 25),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 22),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.YAHTZEE, 50),
                                                new Tuple2<>(Combination.CHANCE, 16)
                                        ),
                                        360,
                                        true
                                ),
                                new Tuple4<>(
                                        List.of(DAVID, EVE, FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.UNITS, 2),
                                                new Tuple2<>(Combination.TWOS, 2),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.FOURS, 8),
                                                new Tuple2<>(Combination.FIVES, 0),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.PAIR, 20),
                                                new Tuple2<>(Combination.TWO_PAIRS, 22),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 24),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 0),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.CHANCE, 22)
                                        ),
                                        222,
                                        false
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_08,
                        "2026-01-08",
                        ALICE,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE, CHARLIE, EVE),
                                        List.of(
                                                new Tuple2<>(Combination.UNITS, 2),
                                                new Tuple2<>(Combination.TWOS, 8),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.FOURS, 8),
                                                new Tuple2<>(Combination.FIVES, 10),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.PAIR, 27),
                                                new Tuple2<>(Combination.TWO_PAIRS, 28),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 27),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 27),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.CHANCE, 19)
                                        ),
                                        278,
                                        false
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_09,
                        "2026-01-09",
                        CHARLIE,
                        List.of(
                                new Tuple4<>(
                                        List.of(CHARLIE),
                                        List.of(
                                                new Tuple2<>(Combination.THREES, 12),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 24),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.TWO_PAIRS, 27),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.SIXES, 24),
                                                new Tuple2<>(Combination.FOURS, 8),
                                                new Tuple2<>(Combination.TWOS, 6),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 25),
                                                new Tuple2<>(Combination.PAIR, 27),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.CHANCE, 27)
                                        ),
                                        326,
                                        true
                                ),
                                new Tuple4<>(
                                        List.of(FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.UNITS, 2),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.THREES, 12),
                                                new Tuple2<>(Combination.TWOS, 4),
                                                new Tuple2<>(Combination.TWO_PAIRS, 23),
                                                new Tuple2<>(Combination.PAIR, 23),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 27),
                                                new Tuple2<>(Combination.CHANCE, 20),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 29),
                                                new Tuple2<>(Combination.FULL_HOUSE, 0),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.FOURS, 8)
                                        ),
                                        251,
                                        false
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_10,
                        "2026-01-10",
                        ALICE,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE, BOB),
                                        List.of(
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.SIXES, 24),
                                                new Tuple2<>(Combination.PAIR, 23),
                                                new Tuple2<>(Combination.TWO_PAIRS, 23),
                                                new Tuple2<>(Combination.YAHTZEE, 50),
                                                new Tuple2<>(Combination.THREES, 6),
                                                new Tuple2<>(Combination.TWOS, 4),
                                                new Tuple2<>(Combination.CHANCE, 21),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 23),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 17),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 0),
                                                new Tuple2<>(Combination.FOURS, 8)
                                        ),
                                        280,
                                        false
                                ),
                                new Tuple4<>(
                                        List.of(DAVID),
                                        List.of(
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.PAIR, 21),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.TWO_PAIRS, 27),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 26),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.FOURS, 16),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 29),
                                                new Tuple2<>(Combination.CHANCE, 26),
                                                new Tuple2<>(Combination.UNITS, 3),
                                                new Tuple2<>(Combination.TWOS, 8),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.THREES, 9)
                                        ),
                                        328,
                                        true
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_11,
                        "2026-01-11",
                        FRANK,
                        List.of(
                                new Tuple4<>(
                                        List.of(FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 29),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 24),
                                                new Tuple2<>(Combination.THREES, 12),
                                                new Tuple2<>(Combination.PAIR, 22),
                                                new Tuple2<>(Combination.TWOS, 8),
                                                new Tuple2<>(Combination.TWO_PAIRS, 18),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.FOURS, 8),
                                                new Tuple2<>(Combination.UNITS, 4),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.FULL_HOUSE, 0),
                                                new Tuple2<>(Combination.CHANCE, 16)
                                        ),
                                        279,
                                        true
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_12,
                        "2026-01-12",
                        DAVID,
                        List.of(
                                new Tuple4<>(
                                        List.of(DAVID, EVE, FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 40),
                                                new Tuple2<>(Combination.TWOS, 8),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.UNITS, 2),
                                                new Tuple2<>(Combination.TWO_PAIRS, 26),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 0),
                                                new Tuple2<>(Combination.SIXES, 18),
                                                new Tuple2<>(Combination.PAIR, 18),
                                                new Tuple2<>(Combination.CHANCE, 26),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 25),
                                                new Tuple2<>(Combination.FOURS, 12)
                                        ),
                                        289,
                                        true
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_13,
                        "2026-01-13",
                        EVE,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 29),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.FOURS, 12),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.TWOS, 4),
                                                new Tuple2<>(Combination.PAIR, 24),
                                                new Tuple2<>(Combination.TWO_PAIRS, 25),
                                                new Tuple2<>(Combination.SIXES, 24),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 29),
                                                new Tuple2<>(Combination.CHANCE, 20),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 0)
                                        ),
                                        282,
                                        true
                                )
                        )
                ),
                createScoresTactileGame(
                        GAME_01_14,
                        "2026-01-14",
                        DAVID,
                        List.of(
                                new Tuple4<>(
                                        List.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK),
                                        List.of(
                                                new Tuple2<>(Combination.UNITS, 1),
                                                new Tuple2<>(Combination.THREES, 9),
                                                new Tuple2<>(Combination.THREE_OF_KIND, 25),
                                                new Tuple2<>(Combination.FULL_HOUSE, 25),
                                                new Tuple2<>(Combination.TWO_PAIRS, 20),
                                                new Tuple2<>(Combination.PAIR, 22),
                                                new Tuple2<>(Combination.SIXES, 24),
                                                new Tuple2<>(Combination.TWOS, 4),
                                                new Tuple2<>(Combination.CHANCE, 24),
                                                new Tuple2<>(Combination.LOW_STRAIGHT, 30),
                                                new Tuple2<>(Combination.FIVES, 15),
                                                new Tuple2<>(Combination.YAHTZEE, 0),
                                                new Tuple2<>(Combination.FOUR_OF_KIND, 0),
                                                new Tuple2<>(Combination.HIGH_STRAIGHT, 0),
                                                new Tuple2<>(Combination.FOURS, 0)
                                        ),
                                        199,
                                        false
                                )
                        )
                ),
                createUntrustedGame(),
                createUntrustedGameWithScores()
        );
    }

    private static final List<Integer> ABSENT_VALUES = List.of(
            200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220,
            221, 223, 224, 225, 226, 227, 228, 229, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243,
            244, 245, 246, 247, 248, 249, 250, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265,
            266, 268, 269, 271, 272, 273, 274, 275, 276, 277, 281, 283, 284, 285, 286, 287, 288, 290, 291, 292, 293,
            294, 295, 296, 297, 299, 300, 301, 302, 303, 304, 306, 307, 309, 311, 312, 313, 314, 315, 316, 317, 318,
            319, 321, 322, 323, 324, 325, 327, 329, 330, 331, 332, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343,
            344, 345, 346, 347, 348, 349, 351, 352, 353, 354, 355, 356, 357, 358, 359, 361, 362, 363, 364, 365, 366,
            367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387,
            388, 389
    );

    @Test
    public void testStatisticsCalculation() {
        Opt<Stats> statsO = statisticsCalculationService.calculateAndGet();
        Assertions.assertTrue(statsO.isPresent());
        Stats stats = statsO.get();
        assertNotEarlierThatMinuteBeforeNow(stats.generatedAt());
        Assertions.assertEquals(10, stats.topLimit());
        Assertions.assertEquals(19, stats.gamesTotal());
        Assertions.assertEquals(24, stats.individualResultsTotal());
        Assertions.assertEquals(13, stats.individualResultsWithScoresTotal());
        Assertions.assertEquals(294.16666f, stats.overallAverage(), 0.00001f);

        Assertions.assertEquals(6, stats.gamesCount().size());
        assertGamesCount(stats.gamesCount(), ALICE, 15);
        assertGamesCount(stats.gamesCount(), BOB, 13);
        assertGamesCount(stats.gamesCount(), CHARLIE, 11);
        assertGamesCount(stats.gamesCount(), DAVID, 11);
        assertGamesCount(stats.gamesCount(), EVE, 11);
        assertGamesCount(stats.gamesCount(), FRANK, 12);

        Assertions.assertEquals(6, stats.average().size());
        assertAverage(1, stats.average(), ALICE, 301.1333333333f);
        assertAverage(3, stats.average(), BOB, 297.4615384615f);
        assertAverage(2, stats.average(), CHARLIE, 300.2727272727f);
        assertAverage(4, stats.average(), DAVID, 282.0909090909f);
        assertAverage(5, stats.average(), EVE, 277.5454545454f);
        assertAverage(6, stats.average(), FRANK, 275.4166666666f);

        Assertions.assertEquals(10, stats.maximum().size());
        assertBound(1, stats.maximum(), Set.of(ALICE), 390, GAME_01_05);
        assertBound(2, stats.maximum(), Set.of(ALICE, BOB, CHARLIE), 360, GAME_01_07);
        assertBound(3, stats.maximum(), Set.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 350, GAME_12_31);
        assertBound(4, stats.maximum(), Set.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 333, GAME_12_30);
        assertBound(5, stats.maximum(), Set.of(DAVID), 328, GAME_01_10);
        assertBound(6, stats.maximum(), Set.of(CHARLIE), 326, GAME_01_09);
        assertBound(7, stats.maximum(), Set.of(ALICE), 320, GAME_01_03);
        assertBound(8, stats.maximum(), Set.of(BOB), 310, GAME_01_03);
        assertBound(9, stats.maximum(), Set.of(BOB), 308, GAME_01_06);
        assertBound(10, stats.maximum(), Set.of(ALICE, BOB, CHARLIE), 305, GAME_01_04);

        Assertions.assertEquals(10, stats.minimum().size());
        assertBound(1, stats.minimum(), Set.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 199, GAME_01_14);
        assertBound(2, stats.minimum(), Set.of(DAVID, EVE, FRANK), 222, GAME_01_07);
        assertBound(3, stats.minimum(), Set.of(DAVID, EVE, FRANK), 230, GAME_01_04);
        assertBound(4, stats.minimum(), Set.of(FRANK), 251, GAME_01_09);
        assertBound(5, stats.minimum(), Set.of(ALICE, BOB, CHARLIE, DAVID, EVE, FRANK), 267, GAME_12_29);
        assertBound(6, stats.minimum(), Set.of(ALICE, BOB), 270, GAME_01_02);
        assertBound(7, stats.minimum(), Set.of(ALICE, CHARLIE, EVE), 278, GAME_01_08);
        assertBound(8, stats.minimum(), Set.of(FRANK), 279, GAME_01_11);
        assertBound(9, stats.minimum(), Set.of(ALICE, BOB), 280, GAME_01_10);
        assertBound(10, stats.minimum(), Set.of(ALICE), 280, GAME_01_01);

        Assertions.assertEquals(5, stats.personalMaximum().size());
        assertPersonalMax(stats.personalMaximum(), ALICE, 390, GAME_01_05);
        assertPersonalMax(stats.personalMaximum(), BOB, 310, GAME_01_03);
        assertPersonalMax(stats.personalMaximum(), CHARLIE, 326, GAME_01_09);
        assertPersonalMax(stats.personalMaximum(), DAVID, 328, GAME_01_10);
        Assertions.assertTrue(stats.personalMaximum().stream().noneMatch(s -> s.getPlayer().equals(EVE)));
        assertPersonalMax(stats.personalMaximum(), FRANK, 279, GAME_01_11);

        Assertions.assertEquals(22, stats.totalDistribution().size());
        assertDistribution(0, stats.totalDistribution(), 199, 1, 4.1666666666f);
        assertDistribution(1, stats.totalDistribution(), 222, 1, 4.1666666666f);
        assertDistribution(2, stats.totalDistribution(), 230, 1, 4.1666666666f);
        assertDistribution(3, stats.totalDistribution(), 251, 1, 4.1666666666f);
        assertDistribution(4, stats.totalDistribution(), 267, 1, 4.1666666666f);
        assertDistribution(5, stats.totalDistribution(), 270, 1, 4.1666666666f);
        assertDistribution(6, stats.totalDistribution(), 278, 1, 4.1666666666f);
        assertDistribution(7, stats.totalDistribution(), 279, 1, 4.1666666666f);
        assertDistribution(8, stats.totalDistribution(), 280, 2, 8.3333333333f);
        assertDistribution(9, stats.totalDistribution(), 282, 1, 4.1666666666f);
        assertDistribution(10, stats.totalDistribution(), 289, 1, 4.1666666666f);
        assertDistribution(11, stats.totalDistribution(), 298, 1, 4.1666666666f);
        assertDistribution(12, stats.totalDistribution(), 305, 2, 8.3333333333f);
        assertDistribution(13, stats.totalDistribution(), 308, 1, 4.1666666666f);
        assertDistribution(14, stats.totalDistribution(), 310, 1, 4.1666666666f);
        assertDistribution(15, stats.totalDistribution(), 320, 1, 4.1666666666f);
        assertDistribution(16, stats.totalDistribution(), 326, 1, 4.1666666666f);
        assertDistribution(17, stats.totalDistribution(), 328, 1, 4.1666666666f);
        assertDistribution(18, stats.totalDistribution(), 333, 1, 4.1666666666f);
        assertDistribution(19, stats.totalDistribution(), 350, 1, 4.1666666666f);
        assertDistribution(20, stats.totalDistribution(), 360, 1, 4.1666666666f);
        assertDistribution(21, stats.totalDistribution(), 390, 1, 4.1666666666f);

        Assertions.assertEquals(11, stats.simpleDistribution().size());
        assertDistribution(0, stats.simpleDistribution(), 39, 1, 7.6923076923f);
        assertDistribution(1, stats.simpleDistribution(), 53, 1, 7.6923076923f);
        assertDistribution(2, stats.simpleDistribution(), 55, 1, 7.6923076923f);
        assertDistribution(3, stats.simpleDistribution(), 57, 1, 7.6923076923f);
        assertDistribution(4, stats.simpleDistribution(), 58, 1, 7.6923076923f);
        assertDistribution(5, stats.simpleDistribution(), 59, 1, 7.6923076923f);
        assertDistribution(6, stats.simpleDistribution(), 64, 2, 15.3846153846f);
        assertDistribution(7, stats.simpleDistribution(), 65, 2, 15.3846153846f);
        assertDistribution(8, stats.simpleDistribution(), 66, 1, 7.6923076923f);
        assertDistribution(9, stats.simpleDistribution(), 69, 1, 7.6923076923f);
        assertDistribution(10, stats.simpleDistribution(), 81, 1, 7.6923076923f);

        Assertions.assertEquals(10, stats.mostFrequentTotalScores().size());
        assertMostFrequentTotalScores(1, stats.mostFrequentTotalScores(), 280, 2);
        assertMostFrequentTotalScores(2, stats.mostFrequentTotalScores(), 305, 2);
        assertMostFrequentTotalScores(3, stats.mostFrequentTotalScores(), 199, 1);
        assertMostFrequentTotalScores(4, stats.mostFrequentTotalScores(), 222, 1);
        assertMostFrequentTotalScores(5, stats.mostFrequentTotalScores(), 230, 1);
        assertMostFrequentTotalScores(6, stats.mostFrequentTotalScores(), 251, 1);
        assertMostFrequentTotalScores(7, stats.mostFrequentTotalScores(), 267, 1);
        assertMostFrequentTotalScores(8, stats.mostFrequentTotalScores(), 270, 1);
        assertMostFrequentTotalScores(9, stats.mostFrequentTotalScores(), 278, 1);
        assertMostFrequentTotalScores(10, stats.mostFrequentTotalScores(), 279, 1);

        Assertions.assertEquals(ABSENT_VALUES, stats.missingValues());

        Assertions.assertEquals(6, stats.simpleDiceAverage().size());
        assertFloatCombination(stats.simpleDiceAverage(), Combination.UNITS, 1.7692307692f);
        assertFloatCombination(stats.simpleDiceAverage(), Combination.TWOS, 2.8461538461f);
        assertFloatCombination(stats.simpleDiceAverage(), Combination.THREES, 3.0769230769f);
        assertFloatCombination(stats.simpleDiceAverage(), Combination.FOURS, 2.3846153846f);
        assertFloatCombination(stats.simpleDiceAverage(), Combination.FIVES, 2.9230769230f);
        assertFloatCombination(stats.simpleDiceAverage(), Combination.SIXES, 3.3846153846f);

        Assertions.assertEquals(6, stats.simpleDiceDistribution().size());
        Assertions.assertEquals(22, stats.simpleDiceDistribution().stream().mapToInt(s -> s.getCounts().size()).sum());
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.UNITS, 1, 6, 46.1538461538f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.UNITS, 2, 5, 38.4615384615f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.UNITS, 3, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.UNITS, 4, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.TWOS, 1, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.TWOS, 2, 4, 30.7692307692f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.TWOS, 3, 4, 30.7692307692f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.TWOS, 4, 4, 30.7692307692f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.THREES, 2, 2, 15.3846153846f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.THREES, 3, 8, 61.5384615385f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.THREES, 4, 3, 23.0769230769f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FOURS, 0, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FOURS, 2, 7, 53.8461538462f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FOURS, 3, 3, 23.0769230769f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FOURS, 4, 2, 15.3846153846f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FIVES, 0, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FIVES, 2, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FIVES, 3, 9, 69.2307692308f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FIVES, 4, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.FIVES, 5, 1, 7.6923076923f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.SIXES, 3, 8, 61.5384615385f);
        assertSimpleDiceDistribution(stats.simpleDiceDistribution(), Combination.SIXES, 4, 5, 38.4615384615f);

        Assertions.assertEquals(5, stats.variableCombinationAverage().size());
        assertFloatCombination(stats.variableCombinationAverage(), Combination.PAIR, 23.3076923076f);
        assertFloatCombination(stats.variableCombinationAverage(), Combination.TWO_PAIRS, 24.7692307692f);
        assertFloatCombination(stats.variableCombinationAverage(), Combination.THREE_OF_KIND, 25.5384615384f);
        assertFloatCombination(stats.variableCombinationAverage(), Combination.FOUR_OF_KIND, 18f);
        assertFloatCombination(stats.variableCombinationAverage(), Combination.CHANCE, 22f);

        Assertions.assertEquals(8, stats.complexCombinationSuccessPercent().size());
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.PAIR, 100f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.TWO_PAIRS, 100f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.THREE_OF_KIND, 100f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.FOUR_OF_KIND, 69.2307692307f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.FULL_HOUSE, 84.6153846153f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.LOW_STRAIGHT, 92.3076923076f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.HIGH_STRAIGHT, 84.6153846153f);
        assertFloatCombination(stats.complexCombinationSuccessPercent(), Combination.YAHTZEE, 30.7692307692f);
    }

    private void assertNotEarlierThatMinuteBeforeNow(Instant instant) {
        Instant now = Instant.now();
        Instant oneMinuteAgo = now.minus(Duration.ofMinutes(1));
        Assertions.assertFalse(
                instant.isBefore(oneMinuteAgo),
                () -> instant + " must be not earlier that " + oneMinuteAgo + ", now: " + now
        );
    }

    private void assertGamesCount(List<GamesCountStats> list, ObjectId player, int expected) {
        list.stream()
                .filter(s -> s.getPlayer().equals(player))
                .findFirst()
                .ifPresentOrElse(
                        s -> Assertions.assertEquals(expected, s.getValue()),
                        () -> Assertions.fail("Player not found: " + player)
                );
    }

    private void assertPersonalMax(
            List<PersonalMaximumStats> list,
            ObjectId player,
            int value,
            ObjectId gameId
    ) {
        list.stream()
                .filter(s -> s.getPlayer().equals(player))
                .findFirst()
                .ifPresentOrElse(
                        s -> {
                            Assertions.assertEquals(value, s.getValue());
                            Assertions.assertEquals(gameId, s.getGameId());
                        }, () -> Assertions.fail("Player not found: " + player)
                );
    }

    private void assertAverage(
            int place,
            List<AverageStats> list,
            ObjectId player,
            float value
    ) {
        AverageStats stats = list.get(place - 1);
        Assertions.assertEquals(player, stats.getPlayer());
        Assertions.assertEquals(value, stats.getValue(), 0.0000000001f);
    }

    private void assertBound(
            int place,
            List<BoundStats> list,
            Set<ObjectId> players,
            int value,
            ObjectId gameId
    ) {
        BoundStats stats = list.get(place - 1);
        Assertions.assertEquals(players, new HashSet<>(stats.getPlayers()));
        Assertions.assertEquals(value, stats.getValue());
        Assertions.assertEquals(gameId, stats.getGameId());
    }

    private void assertMostFrequentTotalScores(
            int place,
            List<MostFrequentTotalScoresStats> list,
            int score,
            int count
    ) {
        MostFrequentTotalScoresStats stats = list.get(place - 1);
        Assertions.assertEquals(score, stats.getTotal());
        Assertions.assertEquals(count, stats.getCount());
    }

    private void assertDistribution(
            int index,
            List<ScoresDistributionStats> list,
            int value,
            int count,
            float percent
    ) {
        ScoresDistributionStats stats = list.get(index);
        Assertions.assertEquals(value, stats.getValue());
        Assertions.assertEquals(count, stats.getCount());
        Assertions.assertEquals(percent, stats.getPercent(), 0.00000001f);
    }

    private void assertFloatCombination(
            List<FloatCombinationStats> list,
            Combination combination,
            float expected
    ) {
        list.stream()
                .filter(s -> s.getCombination() == combination)
                .findFirst()
                .ifPresentOrElse(
                        s -> Assertions.assertEquals(expected, s.getValue(), 0.0000000001f),
                        () -> Assertions.fail("Combination not found: " + combination)
                );
    }

    private void assertSimpleDiceDistribution(
            List<SimpleDiceDistributionStats> list,
            Combination combination,
            int dice,
            int count,
            float percent
    ) {
        SimpleDiceDistributionStats stats = list.stream()
                .filter(s -> s.getCombination() == combination)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Combination not found: " + combination));
        stats.getCounts().stream()
                .filter(s -> s.getDice() == dice)
                .findFirst()
                .ifPresentOrElse(
                        s -> {
                            Assertions.assertEquals(count, s.getCount());
                            Assertions.assertEquals(percent, s.getPercent(), 0.00000001f);
                        }, () -> Assertions.fail("Dice count not found: " + dice)
                );
    }
}
