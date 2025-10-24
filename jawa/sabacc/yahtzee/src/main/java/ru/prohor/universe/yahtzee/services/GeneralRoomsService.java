package ru.prohor.universe.yahtzee.services;

import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.GameRoom;
import ru.prohor.universe.yahtzee.core.data.inner.pojo.RoomReference;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineRoom;

@Service
public class GeneralRoomsService {
    private final MongoRepository<OfflineRoom> tactileOfflineRoomRepository;

    public GeneralRoomsService(MongoRepository<OfflineRoom> tactileOfflineRoomRepository) {
        this.tactileOfflineRoomRepository = tactileOfflineRoomRepository;
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
