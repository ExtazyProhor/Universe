package ru.prohor.universe.yahtzee.offline.data.inner.dto;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OfflineScoreDto {
    private String combination;
    private int value;
}
