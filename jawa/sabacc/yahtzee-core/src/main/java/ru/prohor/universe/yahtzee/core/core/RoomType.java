package ru.prohor.universe.yahtzee.core.core;

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
     * Тактильные кубики, игра онлайн
     */
    TACTILE_ONLINE,

    /**
     * Виртуальные кубики, игра офлайн
     */
    VIRTUAL_OFFLINE;

    public String propertyName() {
        return name().toLowerCase();
    }
}
