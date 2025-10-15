package ru.prohor.universe.yahtzee.services;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.GameRoom;
import ru.prohor.universe.yahtzee.core.RoomType;
import ru.prohor.universe.yahtzee.data.entities.pojo.OfflineRoom;
import ru.prohor.universe.yahtzee.data.inner.pojo.RoomReference;

@Service
public class GeneralRoomsService {
    private final MongoRepository<OfflineRoom> tactileOfflineRoomRepository;

    public GeneralRoomsService(MongoRepository<OfflineRoom> tactileOfflineRoomRepository) {
        this.tactileOfflineRoomRepository = tactileOfflineRoomRepository;
    }

    public static RuntimeException roomNotFound(ObjectId id, RoomType roomType) {
        return new RuntimeException("Server error: " + roomType.propertyName() + " room {" + id + "} not found");
    }

    public Opt<? extends GameRoom> findRoom(Opt<RoomReference> roomRef) {
        if (roomRef.isEmpty())
            return Opt.empty();
        return switch (roomRef.get().type()) {
            case TACTILE_OFFLINE -> tactileOfflineRoomRepository.findById(roomRef.get().id());
            case VIRTUAL_ONLINE, VIRTUAL_OFFLINE, TACTILE_ONLINE -> Opt.empty();
        };
    }
}
