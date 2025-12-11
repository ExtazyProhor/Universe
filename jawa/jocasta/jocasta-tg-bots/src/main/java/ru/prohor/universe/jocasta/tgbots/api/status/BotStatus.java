package ru.prohor.universe.jocasta.tgbots.api.status;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record BotStatus<StatusKey, StatusValue>(StatusKey key, Opt<StatusValue> value) {}
