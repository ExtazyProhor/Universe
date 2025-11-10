package ru.prohor.universe.padawan.scripts.obsidian;

import ru.prohor.universe.jocasta.core.collections.Enumeration;
import ru.prohor.universe.padawan.scripts.obsidian.tasks.AllFoldersHaveDescriptionTask;
import ru.prohor.universe.padawan.scripts.obsidian.tasks.NoUntitledNoteTask;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ObsidianLinter {
    public static final Set<String> EXCLUDED_FOLDERS = Set.of(
            ".git",
            ".obsidian",
            "assets",
            "theory"
    );

    private final List<ObsidianLinterTask> tasks;
    private final Path vault;

    public ObsidianLinter(List<ObsidianLinterTask> tasks, Path vault) {
        this.tasks = tasks;
        this.vault = vault;
    }

    public void validate() {
        Enumeration.enumerate(
                tasks,
                (i, task) -> {
                    System.out.println("--- " + i + ". " + task.name() + " ---");
                    if (!task.checkVault(vault))
                        throw new RuntimeException("Vault validation failure");
                }
        );
    }

    public static void main(String[] args) {
        new ObsidianLinter(
                List.of(
                        new NoUntitledNoteTask(),
                        new AllFoldersHaveDescriptionTask()
                ),
                Path.of(System.getProperty("user.home")).resolve("theory")
        ).validate();
    }
}
