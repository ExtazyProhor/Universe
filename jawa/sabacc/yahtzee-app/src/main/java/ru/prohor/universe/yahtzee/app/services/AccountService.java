package ru.prohor.universe.yahtzee.app.services;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.PaginationResult;
import ru.prohor.universe.jocasta.core.collections.Paginator;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.DiConsumer;
import ru.prohor.universe.jocasta.core.functional.TriFunction;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.hyperspace.jwt.AuthorizedUser;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.MongoTextSearchResult;
import ru.prohor.universe.jocasta.morphia.MongoTransactionService;
import ru.prohor.universe.yahtzee.app.services.images.ImagesService;
import ru.prohor.universe.yahtzee.app.web.api.ColorInfo;
import ru.prohor.universe.yahtzee.app.web.controllers.AccountController;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;
import ru.prohor.universe.yahtzee.core.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.core.services.color.GameColorsService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AccountService {
    private static final int PAGE_SIZE = 5;

    private final MongoTransactionService transactionService;
    private final MongoRepository<Player> playerRepository;
    private final GeneralRoomsService generalRoomsService;
    private final GameColorsService gameColorsService;
    private final ImagesService imagesService;

    public AccountService(
            MongoTransactionService transactionService,
            MongoRepository<Player> playerRepository,
            GeneralRoomsService generalRoomsService,
            GameColorsService gameColorsService,
            ImagesService imagesService
    ) {
        this.transactionService = transactionService;
        this.playerRepository = playerRepository;
        this.generalRoomsService = generalRoomsService;
        this.gameColorsService = gameColorsService;
        this.imagesService = imagesService;
    }

    public Player wrap(AuthorizedUser user) {
        ObjectId objectId = new ObjectId(user.objectId());

        return transactionService.withTransaction(transaction -> {
            MongoRepository<Player> transactional = transaction.wrap(playerRepository);

            return transactional.findById(objectId).orElseGet(() -> {
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
                transactional.save(player);
                return player;
            });
        }).asOpt().orElseThrow(() -> new RuntimeException("Error while requesting user data from the database"));
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
        Opt<TeamColor> color = gameColorsService.getById(player.color());
        return new AccountController.InfoResponse(
                player.username(),
                player.displayName(),
                ColorInfo.of(color),
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

    public ResponseEntity<?> deleteFriend(Player player, String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log
            return ResponseEntity.badRequest().build();
        }

        return transactionService.withTransaction(transaction -> {
            MongoRepository<Player> transactional = transaction.wrap(playerRepository);
            Map<ObjectId, Player> playerAndFriend = transactional.findAllByIdsAsMap(
                    List.of(player.id(), objectId),
                    Player::id
            );
            if (!playerAndFriend.containsKey(objectId))
                return ResponseEntity.notFound().build();
            Player updated = playerAndFriend.get(player.id());
            if (!updated.friends().contains(objectId))
                return ResponseEntity.notFound().build();

            playerRepository.save(List.of(
                    deleteFriend(updated, objectId),
                    deleteFriend(playerAndFriend.get(objectId), updated.id())
            ));
            return ResponseEntity.ok().build();
        }).asOpt().orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private Player deleteFriend(Player player, ObjectId friend) {
        List<ObjectId> friendsIds = new ArrayList<>(player.friends());
        friendsIds.remove(friend);
        return player.toBuilder().friends(friendsIds).build();
    }

    public AccountController.FriendsResponse getFriends(Player player, long page) {
        PaginationResult<ObjectId> paginationResult = Paginator.richPaginateOrLastPage(
                player.friends().stream().sorted().toList(), page, PAGE_SIZE
        );
        return new AccountController.FriendsResponse(
                playerRepository.ensuredFindAllByIds(paginationResult.values())
                        .stream()
                        .sorted(Comparator.comparing(Player::id))
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

    public AccountController.RequestsResponse findRequests(Player player, AccountController.RequestsRequest body) {
        List<ObjectId> requests = body.incoming() ? player.incomingRequests() : player.outcomingRequests();

        PaginationResult<ObjectId> paginationResult = Paginator.richPaginateOrLastPage(
                requests.stream().sorted().toList(), body.page(), PAGE_SIZE
        );
        List<Player> players = playerRepository.ensuredFindAllByIds(paginationResult.values())
                .stream()
                .sorted(Comparator.comparing(Player::id))
                .toList();

        return new AccountController.RequestsResponse(
                players.stream()
                        .map(p -> new AccountController.PlayerRequestInfo(
                                p.id().toHexString(),
                                p.username(),
                                p.displayName(),
                                p.imageId().toHexString()
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
            e.printStackTrace(); // TODO log (SB - Suspicious Behaviour)
            return ResponseEntity.badRequest().build();
        }

        return transactionService.withTransaction(transaction -> {
            MongoRepository<Player> transactional = transaction.wrap(playerRepository);
            Map<ObjectId, Player> playerAndFriend = transactional.findAllByIdsAsMap(
                    List.of(player.id(), objectId),
                    Player::id
            );

            if (!playerAndFriend.containsKey(objectId))
                return ResponseEntity.notFound().build(); // TODO log [SB]
            Player updated = playerAndFriend.get(player.id());
            Player friend = playerAndFriend.get(objectId);

            if (updated.outcomingRequests().contains(objectId))
                return AccountController.REQUEST_ALREADY_EXISTS;
            if (updated.friends().contains(objectId))
                return AccountController.ALREADY_FRIENDS;

            if (updated.incomingRequests().contains(objectId)) {
                addFriend(transactional, friend, updated);
                return AccountController.FRIEND_ADDED;
            }

            editRequest(transactional, updated, friend, List::add);

            return AccountController.REQUEST_SENT;
        }).asOpt().orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    public ResponseEntity<?> retractRequest(Player player, String id) {
        return checkAndEditRequest(
                player,
                id,
                (updated, friend, transactional) -> {
                    if (!updated.outcomingRequests().contains(friend.id()))
                        return ResponseEntity.notFound().build();

                    editRequest(transactional, updated, friend, List::remove);
                    return ResponseEntity.ok().build();
                }
        );
    }

    public ResponseEntity<?> declineRequest(Player player, String id) {
        return checkAndEditRequest(
                player,
                id,
                (updated, friend, transactional) -> {
                    if (!updated.incomingRequests().contains(friend.id()))
                        return ResponseEntity.notFound().build();

                    editRequest(transactional, friend, updated, List::remove);
                    return ResponseEntity.ok().build();
                }
        );
    }

    public ResponseEntity<?> acceptRequest(Player player, String id) {
        return checkAndEditRequest(
                player,
                id,
                (updated, friend, transactional) -> {
                    if (!updated.incomingRequests().contains(friend.id()))
                        return ResponseEntity.notFound().build();

                    addFriend(transactional, friend, updated);
                    return ResponseEntity.ok().build();
                }
        );
    }

    private ResponseEntity<?> checkAndEditRequest(
            Player player,
            String id,
            TriFunction<Player, Player, MongoRepository<Player>, ResponseEntity<?>> playerToFriendFunction
    ) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log (SB - Suspicious Behaviour)
            return ResponseEntity.badRequest().build();
        }

        return transactionService.withTransaction(transaction -> {
            MongoRepository<Player> transactional = transaction.wrap(playerRepository);
            Map<ObjectId, Player> playerAndFriend = transactional.findAllByIdsAsMap(
                    List.of(player.id(), objectId),
                    Player::id
            );

            if (!playerAndFriend.containsKey(objectId))
                return ResponseEntity.notFound().build(); // TODO log [SB]
            Player updated = playerAndFriend.get(player.id());
            Player friend = playerAndFriend.get(objectId);

            return playerToFriendFunction.apply(updated, friend, transactional);
        }).asOpt().orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private void addFriend(MongoRepository<Player> transactional, Player outcoming, Player incoming) {
        List<ObjectId> outcomingRequests = new ArrayList<>(outcoming.outcomingRequests());
        outcomingRequests.remove(incoming.id());
        List<ObjectId> outcomingFriends = new ArrayList<>(outcoming.friends());
        outcomingFriends.add(incoming.id());
        outcoming = outcoming.toBuilder()
                .outcomingRequests(outcomingRequests)
                .friends(outcomingFriends)
                .build();

        List<ObjectId> incomingRequests = new ArrayList<>(incoming.incomingRequests());
        incomingRequests.remove(outcoming.id());
        List<ObjectId> incomingFriends = new ArrayList<>(incoming.friends());
        incomingFriends.add(outcoming.id());
        incoming = incoming.toBuilder()
                .incomingRequests(incomingRequests)
                .friends(incomingFriends)
                .build();

        transactional.save(List.of(outcoming, incoming));
    }

    private void editRequest(
            MongoRepository<Player> transactional,
            Player outcoming,
            Player incoming,
            DiConsumer<List<ObjectId>, ObjectId> action
    ) {
        List<ObjectId> outcomingRequests = new ArrayList<>(outcoming.outcomingRequests());
        action.accept(outcomingRequests, incoming.id());
        outcoming = outcoming.toBuilder()
                .outcomingRequests(outcomingRequests)
                .build();

        List<ObjectId> incomingRequests = new ArrayList<>(incoming.incomingRequests());
        action.accept(incomingRequests, outcoming.id());
        incoming = incoming.toBuilder()
                .incomingRequests(incomingRequests)
                .build();

        transactional.save(List.of(outcoming, incoming));
    }
}
