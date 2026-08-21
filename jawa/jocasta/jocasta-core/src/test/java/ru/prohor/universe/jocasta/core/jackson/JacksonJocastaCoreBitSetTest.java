package ru.prohor.universe.jocasta.core.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

import java.util.BitSet;

@SpringBootTest
@ContextConfiguration(classes = JacksonJocastaCoreConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonJocastaCoreBitSetTest {
    private final ObjectMapper objectMapper;

    public JacksonJocastaCoreBitSetTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldSerializeBitSetAsBase64() throws Exception {
        BitSet value = new BitSet();
        value.set(1);
        value.set(3);
        value.set(10);

        String json = objectMapper.writeValueAsString(value);

        Assertions.assertEquals("\"CgQ=\"", json);
    }

    @Test
    void shouldDeserializeBitSetFromBase64() throws Exception {
        String json = "\"CgQ=\"";
        BitSet value = objectMapper.readValue(json, BitSet.class);

        Assertions.assertTrue(value.get(1));
        Assertions.assertTrue(value.get(3));
        Assertions.assertTrue(value.get(10));
        Assertions.assertFalse(value.get(0));
        Assertions.assertFalse(value.get(2));
        Assertions.assertFalse(value.get(4));
        Assertions.assertEquals(3, value.cardinality());
    }

    @Test
    void shouldSerializeAndDeserializeBitSetInObject() throws Exception {
        BitSet first = new BitSet();
        first.set(1);
        first.set(3);
        first.set(10);

        BitSet second = new BitSet();
        second.set(0);
        second.set(42);
        second.set(169);

        BitSetContainer original = new BitSetContainer(first, second);

        String json = objectMapper.writeValueAsString(original);
        BitSetContainer deserialized = objectMapper.readValue(json, BitSetContainer.class);

        Assertions.assertEquals(original, deserialized);
    }

    @Test
    void shouldSerializeEmptyBitSet() throws Exception {
        BitSet value = new BitSet();
        String json = objectMapper.writeValueAsString(value);
        Assertions.assertEquals("\"\"", json);
    }

    @Test
    void shouldDeserializeEmptyBitSet() throws Exception {
        String json = "\"\"";
        BitSet value = objectMapper.readValue(json, BitSet.class);
        Assertions.assertTrue(value.isEmpty());
    }

    @Test
    void shouldSerializeBitSetWithHighBit() throws Exception {
        BitSet value = new BitSet();
        value.set(169);
        String json = objectMapper.writeValueAsString(value);
        Assertions.assertEquals("\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAg==\"", json);
    }

    @Test
    void shouldDeserializeBitSetWithHighBit() throws Exception {
        String json = "\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAg==\"";
        BitSet value = objectMapper.readValue(json, BitSet.class);
        Assertions.assertTrue(value.get(169));
        Assertions.assertEquals(1, value.cardinality());
        Assertions.assertEquals(169, value.length() - 1);
    }

    private record BitSetContainer(
            BitSet first,
            BitSet second
    ) {}
}
