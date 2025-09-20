package ru.prohor.universe.jocasta.tgbots.api;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TgBotPrefixService<Task extends Identifiable<String>> {
    protected final List<Task> list = new ArrayList<>();

    @SafeVarargs
    public TgBotPrefixService(Task... tasks) {
        list.addAll(Arrays.asList(tasks));
    }

    public Opt<Task> getTask(String prefix) {
        for (Task task : list)
            if (prefix.startsWith(task.getIdentifier()))
                return Opt.of(task);
        return Opt.empty();
    }
}