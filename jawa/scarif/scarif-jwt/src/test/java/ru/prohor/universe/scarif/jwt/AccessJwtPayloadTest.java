package ru.prohor.universe.scarif.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.UUID_VALUE;

class AccessJwtPayloadTest {
    private final ObjectMapper mapper = JwtTestSupport.objectMapper();

    @Test
    void mapsAllClaims() throws Exception {
        ObjectId oid = new ObjectId();
        ObjectId sid = new ObjectId();
        ObjectId jti = new ObjectId();

        String json = """
                {"sub":42,"uid":"%s","oid":"%s","exp":1893456000,"sid":"%s","jti":"%s"}
                """.formatted(UUID_VALUE, oid, sid, jti);

        AccessJwtPayload payload = mapper.readValue(json, AccessJwtPayload.class);

        assertEquals(42L, payload.numericId());
        assertEquals(UUID_VALUE, payload.uuid());
        assertEquals(oid.toHexString(), payload.objectId());

        assertEquals(Instant.ofEpochSecond(1893456000L), payload.expires());
        assertEquals(sid.toHexString(), payload.sessionId());
        assertEquals(jti.toHexString(), payload.jwtId());
    }

    @Test
    void subjectGivenAsStringIsAccepted() throws Exception {
        String json = """
                {"sub":"42","uid":"%s","oid":"o","exp":1893456000,"sid":"s","jti":"j"}
                """.formatted(UUID_VALUE);

        assertEquals(42L, mapper.readValue(json, AccessJwtPayload.class).numericId());
    }
}
