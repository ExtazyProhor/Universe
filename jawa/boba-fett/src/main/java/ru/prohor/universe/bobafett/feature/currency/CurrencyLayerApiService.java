package ru.prohor.universe.bobafett.feature.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CurrencyLayerApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectProvider<FeedbackExecutor> feedbackExecutorProvider;
    private final long adminChatId;
    private final ObjectMapper mapper;
    private final URI uri;

    public CurrencyLayerApiService(
            ObjectProvider<FeedbackExecutor> feedbackExecutorProvider,
            ObjectMapper mapper,
            @Value("${universe.boba-fett.admin-chat-id}") long adminChatId,
            @Value("${universe.boba-fett.currency.base-api-url}") String baseApiUrl,
            @Value("${universe.boba-fett.currency.api-key}") String apiKey
    ) {
        this.feedbackExecutorProvider = feedbackExecutorProvider;
        this.adminChatId = adminChatId;
        this.mapper = mapper;
        this.uri = URI.create(baseApiUrl + "?access_key=" + apiKey + "&source=RUB");
    }

    public List<Rate> getNewRates() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        CurrencyResponse currencyResponse = mapper.readValue(response.body(), CurrencyResponse.class);

        List<StringRate> newRates = currencyResponse.quotes.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() == 6 && entry.getKey().startsWith("RUB"))
                .map(entry -> new StringRate(entry.getKey().substring(3), entry.getValue()))
                .toList();
        checkCurrencies(newRates);
        return newRates.stream()
                .map(rate -> {
                    Opt<Currency> currency = Opt.tryOrNull(() -> Currency.valueOf(rate.code));
                    return currency.map(cur -> new Rate(cur, rate.rateToRussianRuble));
                })
                .filter(Opt::isPresent)
                .map(Opt::get)
                .toList();
    }

    private void checkCurrencies(List<StringRate> rates) {
        Set<String> currenciesFromApi = rates.stream()
                .map(rate -> rate.code)
                .collect(Collectors.toSet());
        Set<String> currenciesFromEnum = Currency.CURRENCIES_FOR_RATES.stream()
                .map(currency -> currency.code)
                .collect(Collectors.toSet());
        if (currenciesFromApi.equals(currenciesFromEnum)) {
            return;
        }

        Set<String> unknownCurrenciesFromApi = new HashSet<>(currenciesFromApi);
        unknownCurrenciesFromApi.removeAll(currenciesFromEnum);
        String unknownCurrenciesMessage = unknownCurrenciesFromApi.isEmpty()
                ? null
                : "Неизвестные валюты из API: " + String.join(", ", unknownCurrenciesFromApi);

        Set<String> notReceivedFromApiCurrencies = new HashSet<>(currenciesFromEnum);
        notReceivedFromApiCurrencies.removeAll(currenciesFromApi);
        String notReceivedCurrenciesMessage = notReceivedFromApiCurrencies.isEmpty()
                ? null
                : "Не пришли валюты: " + String.join(", ", notReceivedFromApiCurrencies);

        String message = String.join(
                "\n\n",
                Stream.of(unknownCurrenciesMessage, notReceivedCurrenciesMessage).filter(Objects::nonNull).toList()
        );
        feedbackExecutorProvider.getObject().sendMessage(adminChatId, message);
    }

    private record CurrencyResponse(Map<String, Double> quotes) {}

    private record StringRate(String code, Double rateToRussianRuble) {}
}
