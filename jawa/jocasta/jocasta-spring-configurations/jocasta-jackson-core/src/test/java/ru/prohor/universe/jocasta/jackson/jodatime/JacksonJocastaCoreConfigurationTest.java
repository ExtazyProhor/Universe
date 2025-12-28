package ru.prohor.universe.jocasta.jackson.jodatime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.prohor.universe.jocasta.core.collections.common.Bool;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreConfigurationTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreConfigurationTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Bool Tests
     */
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

    /**
     * Opt Tests - Empty
     */
    @Test
    void shouldSerializeEmptyOptAsNull() throws Exception {
        Opt<String> empty = Opt.empty();
        String json = objectMapper.writeValueAsString(empty);
        Assertions.assertEquals("null", json);
    }

    @Test
    void shouldDeserializeNullAsEmptyOpt() throws Exception {
        String json = "null";
        Opt<String> opt = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructParametricType(Opt.class, String.class)
        );
        Assertions.assertTrue(opt.isEmpty());
    }

    /**
     * Opt Tests - With Value
     */
    @Test
    void shouldSerializeOptWithStringValue() throws Exception {
        Opt<String> opt = Opt.of("test");
        String json = objectMapper.writeValueAsString(opt);
        Assertions.assertEquals("\"test\"", json);
    }

    @Test
    void shouldDeserializeOptWithStringValue() throws Exception {
        String json = "\"test\"";
        Opt<String> opt = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructParametricType(Opt.class, String.class)
        );
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals("test", opt.get());
    }

    @Test
    void shouldSerializeOptWithIntegerValue() throws Exception {
        Opt<Integer> opt = Opt.of(42);
        String json = objectMapper.writeValueAsString(opt);
        Assertions.assertEquals("42", json);
    }

    @Test
    void shouldDeserializeOptWithIntegerValue() throws Exception {
        String json = "42";
        Opt<Integer> opt = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructParametricType(Opt.class, Integer.class)
        );
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(42, opt.get());
    }

    /**
     * Opt Tests - In Objects
     */
    @Test
    void shouldSerializeOptInObject() throws Exception {
        OptContainer original = new OptContainer(
                Opt.of("present"),
                Opt.empty(),
                Opt.of(123)
        );
        String json = objectMapper.writeValueAsString(original);

        Assertions.assertTrue(json.contains("\"present\""));
        Assertions.assertTrue(json.contains("123"));
    }

    @Test
    void shouldDeserializeOptInObject() throws Exception {
        String json = "{\"presentValue\":\"test\",\"emptyValue\":null,\"numberValue\":999}";
        OptContainer deserialized = objectMapper.readValue(json, OptContainer.class);

        Assertions.assertTrue(deserialized.presentValue.isPresent());
        Assertions.assertEquals("test", deserialized.presentValue.get());
        Assertions.assertTrue(deserialized.emptyValue.isEmpty());
        Assertions.assertTrue(deserialized.numberValue.isPresent());
        Assertions.assertEquals(999, deserialized.numberValue.get());
    }

    @Test
    void shouldDeserializeOptInObjectWithAbsent() throws Exception {
        String jsonNull = "{\"presentValue\":\"test\",\"emptyValue\":null,\"numberValue\":999}";
        String jsonEmpty = "{\"presentValue\":\"test\",\"numberValue\":999}";
        OptContainer deserializedNull = objectMapper.readValue(jsonNull, OptContainer.class);
        OptContainer deserializedEmpty = objectMapper.readValue(jsonEmpty, OptContainer.class);

        Assertions.assertEquals(deserializedNull, deserializedEmpty);
    }

    /**
     * Opt Tests - Complex Types
     */
    @Test
    void shouldSerializeOptWithComplexObject() throws Exception {
        ComplexObject obj = new ComplexObject("name", 100);
        Opt<ComplexObject> opt = Opt.of(obj);
        String json = objectMapper.writeValueAsString(opt);

        Assertions.assertTrue(json.contains("\"name\""));
        Assertions.assertTrue(json.contains("100"));
    }

    @Test
    void shouldDeserializeOptWithComplexObject() throws Exception {
        String json = "{\"name\":\"test\",\"value\":42}";
        Opt<ComplexObject> opt = objectMapper.readValue(
                json,
                objectMapper.getTypeFactory().constructParametricType(Opt.class, ComplexObject.class)
        );

        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals("test", opt.get().name);
        Assertions.assertEquals(42, opt.get().value);
    }

    /**
     * Opt Tests - Nested
     */
    @Test
    void shouldSerializeNestedOpt() throws Exception {
        Opt<Opt<String>> nested = Opt.of(Opt.of("nested"));
        String json = objectMapper.writeValueAsString(nested);
        Assertions.assertEquals("\"nested\"", json);
    }

    @Test
    void shouldSerializeNestedOptWithEmptyInner() throws Exception {
        Opt<Opt<String>> nested = Opt.of(Opt.empty());
        String json = objectMapper.writeValueAsString(nested);
        Assertions.assertEquals("null", json);
    }

    /**
     * Combined Tests
     */
    @Test
    void shouldSerializeCombinedBoolAndOpt() throws Exception {
        CombinedContainer container = new CombinedContainer(
                Bool.TRUE,
                Opt.of("value"),
                Bool.FALSE,
                Opt.empty()
        );

        String json = objectMapper.writeValueAsString(container);
        CombinedContainer deserialized = objectMapper.readValue(json, CombinedContainer.class);

        Assertions.assertTrue(deserialized.boolValue.unwrap());
        Assertions.assertTrue(deserialized.optValue.isPresent());
        Assertions.assertEquals("value", deserialized.optValue.get());
        Assertions.assertFalse(deserialized.anotherBool.unwrap());
        Assertions.assertTrue(deserialized.emptyOpt.isEmpty());
    }

    @Test
    void shouldHandleOptOfBool() throws Exception {
        OptOfBoolContainer container = new OptOfBoolContainer(
                Opt.of(Bool.TRUE),
                Opt.of(Bool.FALSE),
                Opt.empty()
        );

        String json = objectMapper.writeValueAsString(container);
        OptOfBoolContainer deserialized = objectMapper.readValue(json, OptOfBoolContainer.class);

        Assertions.assertTrue(deserialized.trueOpt.isPresent());
        Assertions.assertTrue(deserialized.trueOpt.get().unwrap());
        Assertions.assertTrue(deserialized.falseOpt.isPresent());
        Assertions.assertFalse(deserialized.falseOpt.get().unwrap());
        Assertions.assertTrue(deserialized.emptyOpt.isEmpty());
    }

    /**
     * Test Classes
     */

    private record BoolContainer(
            Bool trueValue,
            Bool falseValue
    ) {}

    private record OptContainer(
            Opt<String> presentValue,
            Opt<String> emptyValue,
            Opt<Integer> numberValue
    ) {}

    private record ComplexObject(
            String name,
            int value

    ) {}

    private record CombinedContainer(
            Bool boolValue,
            Opt<String> optValue,
            Bool anotherBool,
            Opt<String> emptyOpt
    ) {}

    private record OptOfBoolContainer(
            Opt<Bool> trueOpt,
            Opt<Bool> falseOpt,
            Opt<Bool> emptyOpt
    ) {}
}
