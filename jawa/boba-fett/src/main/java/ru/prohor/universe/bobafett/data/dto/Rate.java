package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.prohor.universe.bobafett.data.Currency;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rate {
    private Currency currency;
    @Property("rate_to_russian_ruble")
    private double rateToRussianRuble;
}
