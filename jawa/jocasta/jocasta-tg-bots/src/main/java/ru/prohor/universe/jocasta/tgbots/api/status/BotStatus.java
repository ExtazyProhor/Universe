package ru.prohor.universe.jocasta.tgbots.api.status;

public record BotStatus<StatusKey, StatusValue>(StatusKey key, StatusValue value) {}
