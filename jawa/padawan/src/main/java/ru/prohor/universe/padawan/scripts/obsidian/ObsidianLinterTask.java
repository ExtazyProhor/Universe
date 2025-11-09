package ru.prohor.universe.padawan.scripts.obsidian;

public interface ObsidianLinterTask {
    /**
     * Task name
     */
    String name();

    /**
     * A method for testing obsidian storage according to a certain criterion
     * @return true - if the storage meets the requirements, otherwise - false
     */
    boolean checkVault();
}
