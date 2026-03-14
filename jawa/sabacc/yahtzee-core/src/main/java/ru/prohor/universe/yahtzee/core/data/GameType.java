package ru.prohor.universe.yahtzee.core.data;

public enum GameType {
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

    public boolean isTactile() {
        return this == TACTILE_OFFLINE;
    }

    public boolean isVirtual() {
        return this != TACTILE_OFFLINE;
    }
}
