package ru.prohor.universe.bobafett.feature.currency;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.CurrencyRate;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;
import ru.prohor.universe.jocasta.morphia.query.MongoSorts;

import java.time.Instant;
import java.util.List;

@Service
public class LatestAvailableCurrencyRatesProviderImpl implements LatestAvailableCurrencyRatesProvider {
    private static final long SECONDS_IN_THIRD_OF_DAY = 60 * 60 * 24 / 3;
    private static final MongoQuery<CurrencyRate> LATEST_QUERY = new MongoQuery<CurrencyRate>()
            .sort(MongoSorts.descending(FR.wrap(CurrencyRate::id)))
            .limit(1);

    private final MongoRepository<CurrencyRate> currencyRatesRepository;
    private final CurrencyLayerApiService currencyLayerApiService;

    public LatestAvailableCurrencyRatesProviderImpl(
            MongoRepository<CurrencyRate> currencyRatesRepository,
            CurrencyLayerApiService currencyLayerApiService
    ) {
        this.currencyRatesRepository = currencyRatesRepository;
        this.currencyLayerApiService = currencyLayerApiService;
    }

    @Override
    public CurrencyRate getLatestAvailableCurrencyRates() {
        List<CurrencyRate> latest = currencyRatesRepository.find(LATEST_QUERY);
        if (latest.isEmpty()) {
            return Sneaky.execute(this::getRatesFromApiAndSaveIt);
        }
        if (latest.size() > 1) {
            throw new IllegalStateException("There are more than one rate, but limit was 1");
        }
        CurrencyRate currencyRate = latest.getFirst();

        long now = Instant.now().getEpochSecond();
        if (now - currencyRate.time().getEpochSecond() > SECONDS_IN_THIRD_OF_DAY) {
            try {
                return getRatesFromApiAndSaveIt();
            } catch (Exception e) {
                e.printStackTrace(); // TODO log warn / err
            }
        }
        return currencyRate;
    }

    private CurrencyRate getRatesFromApiAndSaveIt() throws Exception {
        CurrencyRate currencyRate = new CurrencyRate(
                ObjectId.get(),
                Instant.now(),
                currencyLayerApiService.getNewRates()
        );
        currencyRatesRepository.save(currencyRate);
        return currencyRate;
    }
}
