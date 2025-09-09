package ru.prohor.universe.padawan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class TestCiValidation {
    public record Body(
            Response response
    ) {}

    public record Response(
            @JsonProperty("numFound")
            int total,
            List<Artifact> docs
    ) {}

    public record Artifact(
            @JsonProperty("v")
            String version,
            long timestamp
    ) {}

    public static void main(String[] args) throws Exception {
        String s = request("org.springframework.boot", "spring-boot-dependencies").get();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Body body = mapper.readValue(s, Body.class);

        body.response.docs.stream()
                .sorted(Comparator.comparing(Artifact::timestamp))
                .map(Artifact::version)
                .forEach(System.out::println);
    }

    private static final OkHttpClient client = new OkHttpClient();

    public static Opt<String> request(String group, String artifact) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("search.maven.org")
                .addPathSegments("solrsearch/select")
                .addQueryParameter("q", "g:\"" + group + "\" AND a:\"" + artifact + "\"")
                .addQueryParameter("core", "gav")
                .addQueryParameter("rows", "200") // pagination
                .addQueryParameter("wt", "json")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Ошибка запроса: " + response);
            }
            return Opt.ofNullable(response.body()).map(body -> Sneaky.wrap(body::string));
        }
    }
}
