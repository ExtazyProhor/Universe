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
        Opt<String> userAgent,
        String ipAddress,
        boolean closed,
        Opt<Instant> closedAt,
        List<RotatedRefreshToken> recentlyRotatedTokens,
        Opt<ObjectId> refreshToken
) {}
