package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonJodaTimeConfigurationTest {
    private final ObjectMapper objectMapper = new JacksonJodaTimeConfiguration().objectMapper();

    @Test
    void testShouldSerializeInstantToUnixSeconds() throws Exception {
        Instant instant = new Instant(1_000_000L);
        String json = objectMapper.writeValueAsString(instant);
        Assertions.assertEquals("1000", json);
    }

    @Test
    void testShouldDeserializeUnixSecondsToInstant() throws Exception {
        String json = "1000";
        Instant instant = objectMapper.readValue(json, Instant.class);
        Assertions.assertEquals(1_000_000L, instant.getMillis());
    }

    @Test
    void testShouldHandleCurrentTime() throws Exception {
        Instant now = Instant.now();
        String json = objectMapper.writeValueAsString(now);
        Instant restored = objectMapper.readValue(json, Instant.class);
        Assertions.assertEquals((now.getMillis() / 1000) * 1000, restored.getMillis());
    }

    @Test
    void testShouldBeSame() throws Exception {
        String json = "1700000";
        Instant instant = objectMapper.readValue(json, Instant.class);
        String restored = objectMapper.writeValueAsString(instant);
        Assertions.assertEquals(json, restored);
    }
}
