package ru.prohor.universe.yahtzee.app.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilter;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;
import ru.prohor.universe.jocasta.morphia.query.MongoSorts;
import ru.prohor.universe.yahtzee.app.web.controllers.GamesHistoryController;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.core.data.pojo.player.Player;

import java.util.List;

@Service
public class GamesHistoryService { // TODO сделать через page-token-ы (только предыдущая страница и следующая)
    private static final int GAMES_HISTORY_PAGE_SIZE = 10;

    private final MongoRepository<Game> gamesRepository;

    public GamesHistoryService(MongoRepository<Game> gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public ResponseEntity<GamesHistoryController.GamesHistoryResponse> getGamesHistory(Player player, int page) {
        MongoFilter<Game> filter = MongoFilters.contains(FR.wrap(Game::players), player.id());
        int totalCount = (int) gamesRepository.countDocuments(filter);
        int totalPages = Math.ceilDiv(totalCount, GAMES_HISTORY_PAGE_SIZE);
        int lastPage = totalPages - 1; // starts from 0
        if (page > lastPage) {
            page = lastPage;
        }

        MongoQuery<Game> query = new MongoQuery<Game>()
                .filter(filter)
                .limit(GAMES_HISTORY_PAGE_SIZE)
                .sort(MongoSorts.descending(FR.wrap(Game::date)));
        if (page != 0) {
            query.skip(page * GAMES_HISTORY_PAGE_SIZE);
        }

        List<Game> games = gamesRepository.find(query);
        return ResponseEntity.ok(new GamesHistoryController.GamesHistoryResponse(
                totalPages,
                games.stream().map(game -> new GamesHistoryController.GameDescription(
                        game.id(),
                        game.type(),
                        DateTimeUtil.toReadableString(game.date()),
                        game.getTeams().size(),
                        game.players().size(),
                        game.getTeams()
                                .stream()
                                .filter(team -> team.players().contains(player.id()))
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("team with player " + player.id() + "not found")
                                )
                                .total()
                )).toList()
        ));
    }
}
