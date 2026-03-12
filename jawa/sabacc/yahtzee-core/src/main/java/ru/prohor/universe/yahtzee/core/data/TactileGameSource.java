package ru.prohor.universe.yahtzee.core.data;

public enum TactileGameSource {
    /**
     * First few games. Without hasBonus flag, exact scores and time
     */
    OBSIDIAN_TABLE,

    /**
     * Between <code>OBSIDIAN_TABLE</code> and <code>LEGACY_JSON</code>
     */
    SCREENSHOT,

    /**
     * Json-s received from the legacy yahtzee frontend
     */
    LEGACY_JSON,

    /**
     * Saved directly from the backend to the database
     */
    DIRECT
}
