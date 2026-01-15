package ru.prohor.universe.jocasta.jackson.core;

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
public class JacksonJocastaCoreTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
