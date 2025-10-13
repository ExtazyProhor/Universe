package ru.prohor.universe.scarif.data.refresh;

import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.scarif.data.session.Session;

public record RefreshToken(
        long id,
        String token,
        Session session,
        Instant createdAt,
        boolean revoked,
        Opt<Instant> revokedAt
) {
    public RefreshTokenDto toDto() {
        return new RefreshTokenDto(
                id,
                token,
                session.toDto(),
                DateTimeUtil.unwrap(createdAt),
                revoked,
                revokedAt.map(DateTimeUtil::unwrap).orElseNull()
        );
    }

    public static RefreshToken fromDto(RefreshTokenDto refreshTokenDto) {
        return new RefreshToken(
                refreshTokenDto.getId(),
                refreshTokenDto.getToken(),
                Session.fromDto(refreshTokenDto.getSession()),
                DateTimeUtil.wrap(refreshTokenDto.getCreatedAt()),
                refreshTokenDto.isRevoked(),
                Opt.ofNullable(refreshTokenDto.getRevokedAt()).map(DateTimeUtil::wrap)
        );
    }
}
