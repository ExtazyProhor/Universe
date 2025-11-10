package ru.prohor.universe.padawan.scripts.obsidian;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.core.functional.NilPredicate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public interface ObsidianLinterTask {
    /**
     * Task name
     */
    String name();

    /**
     * A method for testing obsidian storage according to a certain criterion
     *
     * @return true - if the storage meets the requirements, otherwise - false
     */
    default boolean checkVault(Path vault) {
        Stream<NilPredicate> stream = Stream.of(
                () -> checkEveryNote(vault),
                () -> checkEveryFolder(vault)
        );
        return stream.allMatch(NilPredicate::test);
    }

    default boolean checkEveryNote(Path vault) {
        Opt<MonoPredicate<Path>> everyNoteChecker = checkEveryNote();
        if (everyNoteChecker.isEmpty())
            return true;
        try {
            Sneaky.execute(() -> {
                Files.walkFileTree(vault, new ObsidianAllNotesVisitor(everyNoteChecker.get()));
            });
            return true;
        } catch (ObsidianLinterException e) {
            return false;
        }
    }

    default boolean checkEveryFolder(Path vault) {
        Opt<MonoPredicate<Path>> everyFolderChecker = checkEveryFolder();
        if (everyFolderChecker.isEmpty())
            return true;
        try (var stream = Files.walk(vault)) {
            Set<String> filter = new HashSet<>(ObsidianLinter.EXCLUDED_FOLDERS);
            return stream.filter(Files::isDirectory)
                    .filter(path -> !filter.remove(path.getFileName().toString()))
                    .allMatch(dir -> everyFolderChecker.get().test(dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default Opt<MonoPredicate<Path>> checkEveryFolder() {
        return Opt.empty();
    }

    default Opt<MonoPredicate<Path>> checkEveryNote() {
        return Opt.empty();
    }
}
