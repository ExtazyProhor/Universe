package ru.prohor.universe.jocasta.tgbots.api.status;

public record TgBotStatus<StatusKey, StatusValue>(StatusKey key, StatusValue value) {}
