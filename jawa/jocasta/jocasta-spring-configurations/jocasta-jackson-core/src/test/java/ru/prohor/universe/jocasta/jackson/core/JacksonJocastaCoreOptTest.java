package ru.prohor.universe.jocasta.jackson.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreOptTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreOptTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

    private record OptContainer(
            Opt<String> presentValue,
            Opt<String> emptyValue,
            Opt<Integer> numberValue
    ) {}

    private record ComplexObject(
            String name,
            int value

    ) {}
}
