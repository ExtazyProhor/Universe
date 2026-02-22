package ru.prohor.universe.jocasta.jackson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.prohor.universe.jocasta.core.collections.common.Bool;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreBoolTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreBoolTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldSerializeBoolTrueAsOne() throws Exception {
        Bool value = Bool.TRUE;
        String json = objectMapper.writeValueAsString(value);
        Assertions.assertEquals("1", json);
    }

    @Test
    void shouldSerializeBoolFalseAsZero() throws Exception {
        Bool value = Bool.FALSE;
        String json = objectMapper.writeValueAsString(value);
        Assertions.assertEquals("0", json);
    }

    @Test
    void shouldDeserializeBoolFromOne() throws Exception {
        String json = "1";
        Bool value = objectMapper.readValue(json, Bool.class);
        Assertions.assertTrue(value.unwrap());
    }

    @Test
    void shouldDeserializeBoolFromZero() throws Exception {
        String json = "0";
        Bool value = objectMapper.readValue(json, Bool.class);
        Assertions.assertFalse(value.unwrap());
    }

    @Test
    void shouldSerializeAndDeserializeBoolInObject() throws Exception {
        BoolContainer original = new BoolContainer(Bool.TRUE, Bool.FALSE);
        String json = objectMapper.writeValueAsString(original);
        BoolContainer deserialized = objectMapper.readValue(json, BoolContainer.class);
        Assertions.assertTrue(deserialized.trueValue.unwrap());
        Assertions.assertFalse(deserialized.falseValue.unwrap());
        Assertions.assertEquals(original, deserialized);
    }

    private record BoolContainer(
            Bool trueValue,
            Bool falseValue
    ) {}
}
