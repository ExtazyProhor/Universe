package ru.prohor.universe.scarif.data;

import lombok.Builder;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record User(
        ObjectId id,
        UUID uuid,
        long numericId,
        Instant createdAt,
        List<ExternalAccount> externalAccounts,
        List<Session> sessions
) {}
