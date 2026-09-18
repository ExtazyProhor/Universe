package ru.prohor.universe.scarif.data;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record Session(
        ObjectId id,
        Instant createdAt,
        Instant expiresAt,
        // TODO https://www.baeldung.com/java-yauaa-user-agent-parsing
        // TODO https://github.com/ua-parser/uap-java
        Opt<String> userAgent,
        String ipAddress,
        // TODO настроить TTL у закрытых или истекших сессий (уровень БД, раз в какое-то долгое время)
        //  например, через месяц после закрытия или истечения
        boolean closed,
        Opt<Instant> closedAt,
        List<RotatedRefreshToken> recentlyRotatedTokens,
        Opt<ObjectId> refreshToken
) {}
