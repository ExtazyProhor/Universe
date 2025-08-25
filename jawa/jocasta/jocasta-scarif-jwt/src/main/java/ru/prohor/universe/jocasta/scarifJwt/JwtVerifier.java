package ru.prohor.universe.jocasta.scarifJwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.jocasta.security.rsa.PublicKeyProvider;

public class JwtVerifier {
    private final PublicKeyProvider keyProvider;
    private final ObjectMapper objectMapper;

    public JwtVerifier(
            PublicKeyProvider keyProvider,
            ObjectMapper objectMapper
    ) {
        this.keyProvider = keyProvider;
        this.objectMapper = objectMapper;
    }

    public Opt<AuthorizedUser> verify(String token) {
        Algorithm algorithm = Algorithm.RSA256(keyProvider.getPublicKey());
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            // TODO тесты на все виды ошибок
            // воссоздать все случаи

            DecodedJWT decodedJWT = verifier.verify(token);
            JwtPayload payload = objectMapper.readValue(
                    new String(Base64.decodeBase64(decodedJWT.getPayload())),
                    JwtPayload.class
            );
            return Opt.of(new AuthorizedUser(
                    payload.id(),
                    payload.uuid(),
                    payload.objectId(),
                    payload.username()
            ));
        } catch (TokenExpiredException e) {
            // TODO log.info или debug (включить в лог саму ошибку)
            System.out.println("Токен просрочен");
        } catch (SignatureVerificationException e) {
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            System.out.println("Подпись неверна");
        } catch (AlgorithmMismatchException e) {
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            System.out.println("Алгоритм подписи не совпадает");
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            System.out.println("Некорректный формат JWT");
        } catch (JWTVerificationException e) {
            // TODO log.error (unknown error) (включить в лог саму ошибку)
            System.out.println("Общая ошибка валидации токена");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO log.error (не должно быть вообще!) (включить в лог саму ошибку)
            System.out.println("невозможно json");
        }
        return Opt.empty();
    }
}
