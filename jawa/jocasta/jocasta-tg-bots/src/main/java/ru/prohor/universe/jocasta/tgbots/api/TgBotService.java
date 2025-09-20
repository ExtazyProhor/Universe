package ru.prohor.universe.jocasta.tgbots.api;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TgBotService<Identifier, Task extends Identifiable<Identifier>> {
    protected final Map<Identifier, Task> tasks = new HashMap<>();

    @SafeVarargs
    public TgBotService(Task... tasks) {
        Arrays.stream(tasks).forEach(x -> this.tasks.put(x.getIdentifier(), x));
    }

    public Opt<Task> getTask(Identifier identifier) {
        return Opt.ofNullable(tasks.get(identifier));
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }
}
