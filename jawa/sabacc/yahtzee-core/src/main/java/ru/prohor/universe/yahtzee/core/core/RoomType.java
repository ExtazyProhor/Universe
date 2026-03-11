package ru.prohor.universe.yahtzee.core.core;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.data.pojo.RoomReference;

public enum RoomType {
    /**
     * Тактильные кубики, игра офлайн
     * (обычный offline)
     */
    TACTILE_OFFLINE,

    /**
     * Виртуальные кубики, игра онлайн
     * (обычный online)
     */
    VIRTUAL_ONLINE,

    /**
     * Виртуальные кубики, игра офлайн
     */
    VIRTUAL_OFFLINE;

    public static RoomType getType(Opt<RoomReference> roomReference) {
        return roomReference.map(RoomReference::type).orElse(RoomType.TACTILE_OFFLINE);
    }
}
