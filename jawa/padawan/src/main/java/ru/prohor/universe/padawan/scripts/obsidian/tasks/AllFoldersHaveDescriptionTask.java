package ru.prohor.universe.padawan.scripts.obsidian.tasks;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.core.utils.NamingStyleUtils;
import ru.prohor.universe.padawan.scripts.obsidian.ObsidianLinterTask;

import java.nio.file.Files;
import java.nio.file.Path;

public class AllFoldersHaveDescriptionTask implements ObsidianLinterTask {
    @Override
    public String name() {
        return "Все папки с заметками имеют заметку-описание";
    }

    @Override
    public Opt<MonoPredicate<Path>> checkEveryFolder() {
        return Opt.of(dir -> {
            String name = dir.getFileName().toString();
            if (!name.toLowerCase().equals(name)) {
                System.out.println("Папка " + name + " содержит заглавные символы в названии");
                return false;
            }
            if (!name.trim().equals(name)) {
                System.out.println("Папка " + name + " содержит пробелы в начале или в конце");
                return false;
            }
            if (!name.matches("^[a-z &\\-]+$")) {
                System.out.println("Запрещенные символы в названии папки: " + name);
                return false;
            }
            name = name.replaceAll(" & ", " ").replaceAll("-", " ");
            String note = NamingStyleUtils.changeStyle(
                    NamingStyleUtils.NamingStyle.LOWER_CASE,
                    NamingStyleUtils.NamingStyle.PASCAL_CASE,
                    name
            );
            if (!Files.isRegularFile(dir.resolve(note))) {
                System.out.println("Заметка с именем " + note + " отсутствует в папке " + name);
                return false;
            }
            return true;
        });
    }
}
