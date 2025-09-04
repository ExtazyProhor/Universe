package ru.prohor.universe.yahtzee.services;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.scarifJwt.AuthorizedUser;
import ru.prohor.universe.yahtzee.data.MongoRepository;
import ru.prohor.universe.yahtzee.data.MongoTextSearchResult;
import ru.prohor.universe.yahtzee.data.entities.pojo.Player;
import ru.prohor.universe.yahtzee.services.color.GameColorsService;
import ru.prohor.universe.yahtzee.services.images.ImagesService;
import ru.prohor.universe.yahtzee.web.controllers.AccountController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {
    private static final int PAGE_SIZE = 5;

    private final MongoRepository<Player> playerRepository;
    private final GameColorsService gameColorsService;
    private final ImagesService imagesService;

    public AccountService(
            MongoRepository<Player> playerRepository,
            GameColorsService gameColorsService,
            ImagesService imagesService
    ) {
        this.playerRepository = playerRepository;
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
                    false
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
        return new AccountController.InfoResponse(
                player.username(),
                player.displayName(),
                gameColorsService.getById(player.color()).background(),
                player.imageId().toHexString()
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
                result.total()
        );
    }

    public boolean addFriend(Player player, String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log
            return false;
        }
        List<ObjectId> ids = new ArrayList<>(player.friends());
        if (ids.contains(objectId)) // TODO log
            return false;
        ids.add(objectId);
        player = player.toBuilder().friends(ids).build();
        playerRepository.save(player);
        return true;
    }

    public boolean deleteFriend(Player player, String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log
            return false;
        }
        List<ObjectId> ids = new ArrayList<>(player.friends());
        if (!ids.remove(objectId))
            return false;
        player = player.toBuilder().friends(ids).build();
        playerRepository.save(player);
        return true;
    }

    public AccountController.FriendsResponse getFriends(Player player, long page) {
        return new AccountController.FriendsResponse(
                player.friends()
                        .stream()
                        .skip(page * PAGE_SIZE)
                        .limit(PAGE_SIZE)
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
                player.friends().size()
        );
    }
}
