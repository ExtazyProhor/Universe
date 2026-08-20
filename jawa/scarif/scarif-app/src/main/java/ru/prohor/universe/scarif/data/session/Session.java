package ru.prohor.universe.scarif.data.session;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.time.Instant;

public record Session(
        long id,
        long userId,
        Instant createdAt,
        Instant expiresAt,
        // TODO https://www.baeldung.com/java-yauaa-user-agent-parsing
        // TODO https://github.com/ua-parser/uap-java
        Opt<String> userAgent,
        String ipAddress,
        boolean closed,
        Opt<Instant> closedAt
) {
    public SessionDto toDto() {
        return new SessionDto(
                id,
                userId,
                createdAt,
                expiresAt,
                userAgent.orElseNull(),
                ipAddress,
                closed,
                closedAt.orElseNull()
        );
    }

    public static Session fromDto(SessionDto sessionDto) {
        return new Session(
                sessionDto.getId(),
                sessionDto.getUserId(),
                sessionDto.getCreatedAt(),
                sessionDto.getExpiresAt(),
                Opt.ofNullable(sessionDto.getUserAgent()),
                sessionDto.getIpAddress(),
                sessionDto.isClosed(),
                Opt.ofNullable(sessionDto.getClosedAt())
        );
    }
}
