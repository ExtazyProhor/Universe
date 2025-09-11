package ru.prohor.universe.scarif.data.session;

import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;

public record Session(
        long id,
        long userId,
        Instant createdAt,
        Instant expiresAt,
        Opt<String> userAgent,
        Opt<String> ipAddress,
        boolean closed,
        Opt<Instant> closedAt
) {
    public SessionDto toDto() {
        return new SessionDto(
                id,
                userId,
                DateTimeUtil.unwrap(createdAt),
                DateTimeUtil.unwrap(expiresAt),
                userAgent.orElseNull(),
                ipAddress.orElseNull(),
                closed,
                closedAt.map(DateTimeUtil::unwrap).orElseNull()
        );
    }

    public static Session fromDto(SessionDto sessionDto) {
        return new Session(
                sessionDto.getId(),
                sessionDto.getUserId(),
                DateTimeUtil.wrap(sessionDto.getCreatedAt()),
                DateTimeUtil.wrap(sessionDto.getExpiresAt()),
                Opt.ofNullable(sessionDto.getUserAgent()),
                Opt.ofNullable(sessionDto.getIpAddress()),
                sessionDto.isClosed(),
                Opt.ofNullable(sessionDto.getClosedAt()).map(DateTimeUtil::wrap)
        );
    }
}
