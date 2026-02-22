package ru.prohor.universe.jocasta.jackson.morphia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@ContextConfiguration(classes = JacksonMorphiaConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class JacksonMorphiaConfigurationTest {
    private final ObjectMapper objectMapper;

    public JacksonMorphiaConfigurationTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldSerializeObjectId() throws Exception {
        ObjectId objectId = new ObjectId("507f1f77bcf86cd799439011");
        String json = objectMapper.writeValueAsString(objectId);
        Assertions.assertEquals("\"507f1f77bcf86cd799439011\"", json);
    }

    @Test
    void shouldDeserializeObjectId() throws Exception {
        String json = "\"507f1f77bcf86cd799439011\"";
        ObjectId objectId = objectMapper.readValue(json, ObjectId.class);
        Assertions.assertNotNull(objectId);
        Assertions.assertEquals("507f1f77bcf86cd799439011", objectId.toHexString());
    }

    @Test
    void shouldSerializeAndDeserializeObjectIdInObject() throws Exception {
        TestObject original = new TestObject(new ObjectId("507f1f77bcf86cd799439011"), "test");

        String json = objectMapper.writeValueAsString(original);
        TestObject deserialized = objectMapper.readValue(json, TestObject.class);

        Assertions.assertNotNull(deserialized);
        Assertions.assertEquals(original.id.toHexString(), deserialized.id.toHexString());
        Assertions.assertEquals(original.name, deserialized.name);
    }

    @Test
    void shouldHandleNullObjectId() throws Exception {
        TestObject original = new TestObject(null, "test");

        String json = objectMapper.writeValueAsString(original);
        TestObject deserialized = objectMapper.readValue(json, TestObject.class);

        Assertions.assertNotNull(deserialized);
        Assertions.assertNull(deserialized.id);
        Assertions.assertEquals(original.name, deserialized.name);
    }

    private record TestObject(
            ObjectId id,
            String name
    ) {}
}
