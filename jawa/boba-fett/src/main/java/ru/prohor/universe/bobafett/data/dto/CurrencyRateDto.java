package ru.prohor.universe.bobafett.data.dto;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity("currency_rates")
public class CurrencyRateDto {
    @Id
    private ObjectId id;
    private Instant time;
    private List<Rate> rates;
}
