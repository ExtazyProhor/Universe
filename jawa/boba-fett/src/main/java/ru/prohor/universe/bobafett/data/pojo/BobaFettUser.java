package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.bobafett.data.dto.BobaFettUserDto;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

@Builder(toBuilder = true)
public record BobaFettUser(
        ObjectId id,
        long chatId,
        String chatType,
        Opt<String> chatName,
        Opt<String> userLink,
        Opt<HolidaysSubscriptionOptions> holidaysSubscriptionOptions,
        Opt<UserStatus> status
) implements MongoEntityPojo<BobaFettUserDto> {
    @Override
    public BobaFettUserDto toDto() {
        return new BobaFettUserDto(
                id,
                chatId,
                chatType,
                chatName.orElseNull(),
                userLink.orElseNull(),
                holidaysSubscriptionOptions.map(HolidaysSubscriptionOptions::toDto).orElseNull(),
                status.map(UserStatus::toDto).orElseNull()
        );
    }

    public static BobaFettUser fromDto(BobaFettUserDto user) {
        return new BobaFettUser(
                user.getId(),
                user.getChatId(),
                user.getChatType(),
                Opt.ofNullable(user.getChatName()),
                Opt.ofNullable(user.getUserLink()),
                Opt.ofNullable(user.getHolidaysSubscriptionOptions()).map(HolidaysSubscriptionOptions::fromDto),
                Opt.ofNullable(user.getStatus()).map(UserStatus::fromDto)
        );
    }
}
