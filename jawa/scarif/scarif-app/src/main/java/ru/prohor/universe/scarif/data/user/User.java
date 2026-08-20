package ru.prohor.universe.scarif.data.user;

import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.UUID;

public record User(
        long id,
        UUID uuid,
        ObjectId objectId,
        String username, // TODO хранить lowercase username чтобы не дублировались
        String email,
        String password,
        boolean enabled,
        Instant createdAt
) {
    public UserDto toDto() {
        return new UserDto(
                id,
                uuid,
                objectId.toString(),
                username,
                email,
                password,
                enabled,
                createdAt
        );
    }

    public static User fromDto(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getUuid(),
                new ObjectId(userDto.getObjectId()),
                userDto.getUsername(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.isEnabled(),
                userDto.getCreatedAt()
        );
    }
}
