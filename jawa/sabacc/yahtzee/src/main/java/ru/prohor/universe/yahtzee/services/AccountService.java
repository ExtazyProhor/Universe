package ru.prohor.universe.yahtzee.services;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.PaginationResult;
import ru.prohor.universe.jocasta.core.collections.Paginator;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.jocasta.jwt.AuthorizedUser;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTextSearchResult;
import ru.prohor.universe.yahtzee.core.TeamColor;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.services.images.ImagesService;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private static final int PAGE_SIZE = 5;
    private static final int TRANSACTION_RETRIES = 2;

    private final MongoRepository<Player> playerRepository;
    private final GeneralRoomsService generalRoomsService;
    private final GameColorsService gameColorsService;
    private final ImagesService imagesService;

    public AccountService(
            MongoRepository<Player> playerRepository,
            GeneralRoomsService generalRoomsService,
            GameColorsService gameColorsService,
            ImagesService imagesService
    ) {
        this.playerRepository = playerRepository;
        this.generalRoomsService = generalRoomsService;
        this.gameColorsService = gameColorsService;
        this.imagesService = imagesService;
    }

    public Player wrap(AuthorizedUser user) {
        ObjectId objectId = new ObjectId(user.objectId());
        return playerRepository.findById(objectId).orElseGet(() -> {
            Player player = new Player(
                    objectId,
                    user.uuid(),
                    user.id(),
                    user.username(),
                    gameColorsService.getRandomColorId(),
                    user.username(),
                    List.of(),
                    Opt.empty(),
                    imagesService.generateAndSave().id(),
                    Instant.now(),
                    false,
                    Collections.emptyList(),
                    Collections.emptyList()
            );
            playerRepository.save(player);
            return player;
        });
    }

    public boolean changeName(Player player, String name) {
        playerRepository.save(player.toBuilder().displayName(name).build());
        return true;
    }

    public boolean changeColor(Player player, int colorId) {
        if (!gameColorsService.validateColor(colorId))
            return false;
        playerRepository.save(player.toBuilder().color(colorId).build());
        return true;
    }

    public AccountController.InfoResponse getPlayerInfo(Player player) {
        TeamColor color = gameColorsService.getById(player.color());
        return new AccountController.InfoResponse(
                player.username(),
                player.displayName(),
                color.background(),
                color.text(),
                player.imageId().toHexString(),
                generalRoomsService.findRoom(player.currentRoom()).map(
                        room -> new AccountController.RoomInfo(
                                room.type().propertyName(),
                                DateTimeUtil.toReadableString(room.createdAt()),
                                room.teamsCount()
                        )
                ).orElseNull(),
                player.incomingRequests().size()
        );
    }

    public AccountController.FindUsersResponse findUsers(Player player, String name, int page) {
        Set<ObjectId> friends = new HashSet<>(player.friends());
        MongoTextSearchResult<Player> result = playerRepository.findByText(name, page, PAGE_SIZE);
        return new AccountController.FindUsersResponse(
                result.entities()
                        .stream()
                        .filter(p -> !p.id().equals(player.id()))
                        .map(p -> new AccountController.FoundUser(
                                p.id().toHexString(),
                                p.username(),
                                p.displayName(),
                                p.imageId().toHexString(),
                                friends.contains(p.id())
                        ))
                        .toList(),
                result.page(),
                result.lastPage()
        );
    }

    // TODO transaction
    public ResponseEntity<?> deleteFriend(Player player, String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log
            return ResponseEntity.badRequest().build();
        }
        // TODO requery
        List<ObjectId> ids = new ArrayList<>(player.friends());
        if (!ids.remove(objectId))
            return ResponseEntity.notFound().build();

        player = player.toBuilder().friends(ids).build();
        Player friend = playerRepository.findById(objectId).get(); // TODO mongo ensure
        ids = new ArrayList<>(friend.friends());
        ids.remove(player.id());
        friend = friend.toBuilder().friends(ids).build();
        playerRepository.save(List.of(player, friend)); // TODO в отдельный сервис
        return ResponseEntity.ok().build();
    }

    public AccountController.FriendsResponse getFriends(Player player, long page) {
        PaginationResult<ObjectId> paginationResult = Paginator.richPaginateOrLastPage(
                player.friends().stream().sorted().toList(), page, PAGE_SIZE
        );
        return new AccountController.FriendsResponse(
                paginationResult.values()
                        .stream()
                        .map(id -> playerRepository.findById(id).orElseThrow(
                                () -> new RuntimeException("Server error: Player {" + id + "} now found")
                        ))
                        .map(p -> new AccountController.Friend(
                                p.id().toHexString(),
                                p.username(),
                                p.displayName(),
                                p.imageId().toHexString(),
                                p.currentRoom().isPresent()
                        ))
                        .toList(),
                paginationResult.page(),
                paginationResult.lastPage()
        );
    }

    // TODO transaction
    public AccountController.RequestsResponse findRequests(Player player, AccountController.RequestsRequest body) {
        List<ObjectId> players = body.incoming() ? player.incomingRequests() : player.outcomingRequests();
        PaginationResult<ObjectId> paginationResult = Paginator.richPaginateOrLastPage(players, body.page(), PAGE_SIZE);
        Map<ObjectId, Player> idPlayerMap = playerRepository.findAllByIds(paginationResult.values())
                .stream()
                .collect(Collectors.toMap(Player::id, Function.identity()));

        return new AccountController.RequestsResponse(
                paginationResult.values()
                        .stream()
                        .map(id -> Opt.ofNullable(idPlayerMap.get(id)))
                        .filter(playerO -> {
                            if (playerO.isEmpty())
                                System.out.println("player is null"); // TODO mongo ensure
                            return playerO.isPresent();
                        })
                        .map(playerO -> new AccountController.PlayerRequestInfo(
                                playerO.get().id().toHexString(),
                                playerO.get().username(),
                                playerO.get().displayName(),
                                playerO.get().imageId().toHexString()
                        ))
                        .toList(),
                paginationResult.page(),
                paginationResult.lastPage()
        );
    }

    public ResponseEntity<?> sendRequest(Player player, String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log
            // TODO мб временный бан user-а (SB - Suspicious Behaviour)
            return ResponseEntity.badRequest().build();
        }

        // TODO транзакция, сначала перезапрос пользователя + друга
        player = player; // TODO
        Opt<Player> target = playerRepository.findById(objectId);
        if (target.isEmpty())
            return ResponseEntity.notFound().build();
        Player friend = target.get();
        // TODO метод ensure в mongo, который будет падать если не найдет юзера
        Set<ObjectId> outcomingRequests = new HashSet<>(player.outcomingRequests());
        if (outcomingRequests.contains(objectId))
            return AccountController.REQUEST_ALREADY_EXISTS;

        Set<ObjectId> incomingRequests = new HashSet<>(player.incomingRequests());
        if (incomingRequests.contains(objectId)) {
            // TODO вынести эту логику в отдельный класс `FriendRequestsService`
            incomingRequests.remove(objectId);
            List<ObjectId> friends = new ArrayList<>(player.friends());
            friends.add(objectId);
            player = player.toBuilder()
                    .incomingRequests(incomingRequests.stream().toList())
                    .friends(friends)
                    .build();
            incomingRequests = new HashSet<>(target.get().incomingRequests());
            friends = new ArrayList<>(friend.friends());
            friends.add(player.id());
            friend = friend.toBuilder()
                    .incomingRequests(incomingRequests.stream().toList())
                    .friends(friends)
                    .build();
            playerRepository.save(List.of(player, friend));
            // TODO создать record с результатом. Тут - добавление в друзья
            return ResponseEntity.ok("{\"result\": \"added\"}");
        }

        Set<ObjectId> friends = new HashSet<>(player.friends());
        if (friends.contains(objectId))
            return AccountController.ALREADY_FRIENDS;

        outcomingRequests.add(objectId);
        incomingRequests = new HashSet<>(friend.incomingRequests());
        incomingRequests.add(player.id());

        player = player.toBuilder().outcomingRequests(outcomingRequests.stream().toList()).build();
        friend = friend.toBuilder().incomingRequests(incomingRequests.stream().toList()).build();
        playerRepository.save(List.of(player, friend));
        // TODO а тут - отправка заявки
        return ResponseEntity.ok("{\"result\": \"requested\"}");
    }

    public ResponseEntity<?> retractRequest(Player player, String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // TODO
    }

    public ResponseEntity<?> declineRequest(Player player, String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // TODO
    }

    public ResponseEntity<?> acceptRequest(Player player, String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // TODO
    }
}
