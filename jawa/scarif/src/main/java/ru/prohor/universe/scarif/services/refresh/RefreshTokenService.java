package ru.prohor.universe.scarif.services.refresh;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;

import java.security.SecureRandom;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class RefreshTokenService {
    private static final Predicate<String> USER_TOKEN_FORMAT =
            Pattern.compile("\\d{1,19}:[0-9a-f]{64}").asMatchPredicate();

    private final int refreshTokenSizeBytes;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SecureRandom random;

    public RefreshTokenService(
            @Value("${universe.scarif.refreshTokenSizeBytes}") int refreshTokenSizeBytes,
            SnowflakeIdGenerator snowflakeIdGenerator
    ) {
        this.refreshTokenSizeBytes = refreshTokenSizeBytes;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.random = new SecureRandom();
    }

    public Tokens generateTokens() {
        // TODO сделать генерацию не по времени (ИБ)
        long id = snowflakeIdGenerator.nextId();
        byte[] token = new byte[refreshTokenSizeBytes];
        random.nextBytes(token);
        String tokenHex = Hex.encodeHexString(token);
        String tokenHash = DigestUtils.sha256Hex(token);
        return new Tokens(id + ":" + tokenHex, id, tokenHash);
    }

    public ParsedUserToken parseUserToken(String userToken) throws UserTokenParsingException {
        if (!USER_TOKEN_FORMAT.test(userToken))
            throw new UserTokenParsingException("userToken", userToken);
        long id;
        try {
            id = Long.parseLong(userToken.substring(0, userToken.indexOf(':')));
        } catch (NumberFormatException e) {
            throw new UserTokenParsingException("id", userToken);
        }
        String token = userToken.substring(userToken.indexOf(':') + 1);
        try {
            byte[] bytes = Hex.decodeHex(token);
            String tokenHash = DigestUtils.sha256Hex(bytes);
            return new ParsedUserToken(
                    id,
                    refreshToken -> refreshToken.token().equals(tokenHash)
            );
        } catch (DecoderException e) {
            throw new UserTokenParsingException("token", token);
        }
    }
}
