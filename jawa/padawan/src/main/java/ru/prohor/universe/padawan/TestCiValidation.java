package ru.prohor.universe.padawan;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class TestCiValidation {
    public record Metadata(
            String groupId,
            String artifactId,
            Versioning versioning
    ) {}

    public record Versioning(
            List<String> versions,
            String latest,
            String release,
            String lastUpdated
    ) {}

    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        Metadata metadata = request("org.springframework.boot", "spring-boot-dependencies");

        metadata.versioning.versions.stream()
                .sorted(Comparator.naturalOrder())
                .forEach(System.out::println);

        System.out.println(metadata);
    }

    public static Metadata request(String group, String artifact) throws IOException {
        String path = group.replace('.', '/') + "/" + artifact + "/maven-metadata.xml";

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("repo1.maven.org")
                .addPathSegments("maven2/" + path)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Ошибка запроса: " + response);
            }

            String xml = response.body().string();
            XmlMapper mapper = new XmlMapper();
            return mapper.readValue(xml, Metadata.class);
        }
    }
}
