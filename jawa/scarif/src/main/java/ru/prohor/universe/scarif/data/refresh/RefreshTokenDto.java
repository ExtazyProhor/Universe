package ru.prohor.universe.scarif.data.refresh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.prohor.universe.scarif.data.session.SessionDto;

import java.time.Instant;

// TODO индексы на всех таблицах
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenDto {
    @Id
    @Column(nullable = false, unique = true)
    private long id;

    @Column(nullable = false, updatable = false)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false, updatable = false)
    private SessionDto session;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @SuppressWarnings("unused")
    public RefreshTokenDto() {
    }

    public RefreshTokenDto(
            long id,
            String token,
            SessionDto session,
            Instant createdAt,
            boolean revoked,
            Instant revokedAt
    ) {
        this.id = id;
        this.token = token;
        this.session = session;
        this.createdAt = createdAt;
        this.revoked = revoked;
        this.revokedAt = revokedAt;
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public SessionDto getSession() {
        return session;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }
}
