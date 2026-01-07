package ru.prohor.universe.yahtzee.app.services;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.app.web.controllers.OfflineGameInfoController;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;

@Service
public class OfflineGameInfoService {
    private final MongoRepository<OfflineGame> gameRepository;

    public OfflineGameInfoService(MongoRepository<OfflineGame> gameRepository) {
        this.gameRepository = gameRepository;
    }

    public ResponseEntity<OfflineGameInfoController.OfflineGameInfoResponse> getOfflineGameInfo(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log SB
            return ResponseEntity.badRequest().build();
        }
        return gameRepository.findById(objectId)
                .map(this::mapGame)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private OfflineGameInfoController.OfflineGameInfoResponse mapGame(OfflineGame game) {
        return new OfflineGameInfoController.OfflineGameInfoResponse();
    }
}
