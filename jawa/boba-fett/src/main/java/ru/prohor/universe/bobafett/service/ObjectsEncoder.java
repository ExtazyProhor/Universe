package ru.prohor.universe.bobafett.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

@Service
public class ObjectsEncoder {
    private final ObjectMapper objectMapper;

    public ObjectsEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encode(Object object) {
        String json = Sneaky.execute(() -> objectMapper.writeValueAsString(object));
        return Base64.encodeBase64String(json.getBytes());
    }
}
