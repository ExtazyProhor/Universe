package ru.prohor.universe.scarif.data.refresh;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.data.session.Session;

import java.time.Instant;

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
                createdAt,
                revoked,
                revokedAt.orElseNull()
        );
    }

    public static RefreshToken fromDto(RefreshTokenDto refreshTokenDto) {
        return new RefreshToken(
                refreshTokenDto.getId(),
                refreshTokenDto.getToken(),
                Session.fromDto(refreshTokenDto.getSession()),
                refreshTokenDto.getCreatedAt(),
                refreshTokenDto.isRevoked(),
                Opt.ofNullable(refreshTokenDto.getRevokedAt())
        );
    }
}
