package ru.prohor.universe.padawan.scripts.obsidian;

import ru.prohor.universe.jocasta.core.collections.Enumeration;

import java.util.List;

public class ObsidianLinter {
    private final List<ObsidianLinterTask> tasks;

    public ObsidianLinter(List<ObsidianLinterTask> tasks) {
        this.tasks = tasks;
    }

    public void validate() {
        Enumeration.enumerate(tasks, (i, task) -> {
            System.out.println("--- " + i + ". " + task.name() + " ---");
            if (!task.checkVault())
                throw new RuntimeException("Vault validation failure");
        });
    }

    public static void main(String[] args) {
        ObsidianLinter linter = new ObsidianLinter(List.of(

        ));
        linter.validate();
    }
}
