package ru.prohor.universe.scarif.data.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserDto {
    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true, columnDefinition = "uuid")
    private UUID uuid;

    @Column(name = "object_id", updatable = false, nullable = false, unique = true)
    private String objectId;

    @Column(updatable = false, nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @SuppressWarnings("unused")
    public UserDto() {
    }

    public UserDto(
            Long id,
            UUID uuid,
            String objectId,
            String username,
            String email,
            String password,
            boolean enabled,
            Instant createdAt
    ) {
        this.id = id;
        this.uuid = uuid;
        this.objectId = objectId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
