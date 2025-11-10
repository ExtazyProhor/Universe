package ru.prohor.universe.padawan.scripts.obsidian.tasks;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.padawan.scripts.obsidian.ObsidianLinterTask;

import java.nio.file.Path;

public class NoUntitledNoteTask implements ObsidianLinterTask {
    @Override
    public String name() {
        return "Проверка отсутствия безымянной заметки";
    }

    @Override
    public Opt<MonoPredicate<Path>> checkEveryNote() {
        return Opt.of(path -> {
            String name = path.getFileName().toString();
            return !name.equalsIgnoreCase("Untitled.md") && !name.equalsIgnoreCase("Без названия.md");
        });
    }
}
