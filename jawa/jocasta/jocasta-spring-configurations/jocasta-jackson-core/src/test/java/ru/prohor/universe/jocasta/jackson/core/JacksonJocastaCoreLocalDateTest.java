package ru.prohor.universe.jocasta.jackson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreLocalDateTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreLocalDateTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldSerializeLocalDateToIsoString() throws Exception {
        LocalDate date = LocalDate.of(2026, 1, 15);
        String json = objectMapper.writeValueAsString(date);
        Assertions.assertEquals("\"2026-01-15\"", json);
    }

    @Test
    void shouldDeserializeLocalDateFromIsoString() throws Exception {
        String json = "\"2026-01-15\"";
        LocalDate date = objectMapper.readValue(json, LocalDate.class);
        Assertions.assertEquals(LocalDate.of(2026, 1, 15), date);
    }

    @Test
    void shouldSerializeAndDeserializeClassWithLocalTime() throws Exception {
        TestClassWithLocalDate dto = new TestClassWithLocalDate();
        dto.date = LocalDate.of(2026, 1, 15);

        String json = objectMapper.writeValueAsString(dto);
        TestClassWithLocalDate restored = objectMapper.readValue(json, TestClassWithLocalDate.class);

        Assertions.assertEquals(dto.date, restored.date);
    }


    static class TestClassWithLocalDate {
        public LocalDate date;
    }
}
