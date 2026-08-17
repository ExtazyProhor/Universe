package ru.prohor.universe.bobafett.data.pojo;

import lombok.Builder;
import org.bson.types.ObjectId;
import ru.prohor.universe.bobafett.data.dto.CurrencyRateDto;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.morphia.MongoEntityPojo;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record CurrencyRate(
        ObjectId id,
        Instant time,
        List<Rate> rates
) implements MongoEntityPojo<CurrencyRateDto> {
    @Override
    public CurrencyRateDto toDto() {
        return new CurrencyRateDto(id, time, rates);
    }

    public static CurrencyRate fromDto(CurrencyRateDto currencyRate) {
        return new CurrencyRate(
                currencyRate.getId(),
                currencyRate.getTime(),
                currencyRate.getRates()
        );
    }
}
