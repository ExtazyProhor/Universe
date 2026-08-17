package ru.prohor.universe.bobafett.feature.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyLayerApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;
    private final URI uri;

    public CurrencyLayerApiService(
            ObjectMapper mapper,
            @Value("${universe.boba-fett.currency.base-api-url}") String baseApiUrl,
            @Value("${universe.boba-fett.currency.api-key}") String apiKey
    ) {
        this.mapper = mapper;
        this.uri = generateUri(baseApiUrl, apiKey);
    }

    private URI generateUri(String baseApiUrl, String apiKey) {
        String currencies = Currency.CURRENCIES_FOR_RATES
                .stream()
                .map(currency -> currency.code)
                .collect(Collectors.joining(","));
        return Sneaky.execute(
                () -> new URIBuilder(baseApiUrl)
                        .addParameter("access_key", apiKey)
                        .addParameter("source", "RUB")
                        .addParameter("currencies", currencies)
                        .addParameter("format", "1")
                        .build()
        );
    }

    public List<Rate> getNewRates() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        CurrencyResponse currencyResponse = mapper.readValue(response.body(), CurrencyResponse.class);

        return currencyResponse.quotes.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() == 6 && entry.getKey().startsWith("RUB"))
                .map(entry -> {
                    String code = entry.getKey().substring(3);
                    Opt<Currency> currency = Opt.tryOrNull(() -> Currency.valueOf(code));
                    return currency.map(cur -> new Rate(cur, entry.getValue()));
                })
                .filter(Opt::isPresent)
                .map(Opt::get)
                .toList();
    }

    private record CurrencyResponse(Map<String, Double> quotes) {}
}
