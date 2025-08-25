package ru.prohor.universe.scarif.data.session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

// TODO и нужен ли String полям columnDefinition = "text"
@Entity
@Table(name = "sessions")
public class SessionDto {
    @Id
    @Column(nullable = false, unique = true)
    private long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "user_agent", updatable = false)
    private String userAgent;

    @Column(name = "ip_address", updatable = false)
    private String ipAddress;

    @Column(nullable = false)
    private boolean closed;

    @Column(name = "closed_at")
    private Instant closedAt;

    @SuppressWarnings("unused")
    public SessionDto() {
    }

    public SessionDto(
            long id,
            long userId,
            Instant createdAt,
            Instant expiresAt,
            String userAgent,
            String ipAddress,
            boolean revoked,
            Instant closedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.closed = revoked;
        this.closedAt = closedAt;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isClosed() {
        return closed;
    }

    public Instant getClosedAt() {
        return closedAt;
    }
}
