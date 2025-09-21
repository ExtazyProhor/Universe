package ru.prohor.universe.jocasta.tgbots.api;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TgBotService<I, T extends Identifiable<I>> {
    protected final Map<I, T> tasks = new HashMap<>();

    @SafeVarargs
    public TgBotService(T... ts) {
        Arrays.stream(ts).forEach(x -> this.tasks.put(x.getIdentifier(), x));
    }

    public Opt<T> getTask(I identifier) {
        return Opt.ofNullable(tasks.get(identifier));
    }

    public Collection<T> getAllTasks() {
        return tasks.values();
    }
}
