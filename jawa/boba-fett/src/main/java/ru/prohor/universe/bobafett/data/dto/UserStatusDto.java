package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserStatusDto {
    private String key;
    private String value;
}
