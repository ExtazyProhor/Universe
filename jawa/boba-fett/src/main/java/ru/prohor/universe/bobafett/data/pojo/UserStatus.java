package ru.prohor.universe.bobafett.data.pojo;

import ru.prohor.universe.bobafett.data.dto.UserStatusDto;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

public record UserStatus(String key, Opt<String> value) implements MongoEntityPojo<UserStatusDto> {
    @Override
    public UserStatusDto toDto() {
        return new UserStatusDto(key, value.orElseNull());
    }

    public static UserStatus fromDto(UserStatusDto userStatus) {
        return new UserStatus(
                userStatus.getKey(),
                Opt.ofNullable(userStatus.getValue())
        );
    }
}
