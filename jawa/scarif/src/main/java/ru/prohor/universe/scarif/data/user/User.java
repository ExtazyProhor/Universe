package ru.prohor.universe.scarif.data.user;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.jodaTime.DateTimeUtil;

import java.util.UUID;

public record User(
        long id,
        UUID uuid,
        ObjectId objectId,
        String username,
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
                DateTimeUtil.unwrap(createdAt)
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
                DateTimeUtil.wrap(userDto.getCreatedAt())
        );
    }
}
