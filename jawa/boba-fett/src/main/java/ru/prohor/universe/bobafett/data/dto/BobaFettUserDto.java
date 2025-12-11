package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("users")
public class BobaFettUserDto {
    @Id
    private ObjectId id;
    @Property("chat_id")
    private long chatId;
    @Property("chat_type")
    private String chatType;
    @Property("chat_name")
    private String chatName;
    @Property("user_link")
    private String userLink;
    @Property("holidays_subscription_options")
    private HolidaysSubscriptionOptionsDto holidaysSubscriptionOptions;
    private UserStatusDto status;
}
