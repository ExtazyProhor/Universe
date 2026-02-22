package ru.prohor.universe.jocasta.jackson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreInstantTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreInstantTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldSerializeInstantToEpochSeconds() throws Exception {
        long timestamp = 1705492800L;
        Instant instant = Instant.ofEpochSecond(timestamp);
        String json = objectMapper.writeValueAsString(instant);
        Assertions.assertEquals(String.valueOf(timestamp), json);
    }

    @Test
    void shouldDeserializeInstantFromEpochSeconds() throws Exception {
        String json = "1705492800";
        Instant instant = objectMapper.readValue(json, Instant.class);
        Assertions.assertEquals(Instant.ofEpochSecond(Long.parseLong(json)), instant);
    }

    @Test
    void shouldSerializeAndDeserializeClassWithInstant() throws Exception {
        TestClassWithLocalInstant dto = new TestClassWithLocalInstant();
        dto.createdAt = Instant.ofEpochSecond(1705492800);

        String json = objectMapper.writeValueAsString(dto);
        TestClassWithLocalInstant restored = objectMapper.readValue(json, TestClassWithLocalInstant.class);

        Assertions.assertEquals(dto.createdAt, restored.createdAt);
    }

    static class TestClassWithLocalInstant {
        public Instant createdAt;
    }
}
