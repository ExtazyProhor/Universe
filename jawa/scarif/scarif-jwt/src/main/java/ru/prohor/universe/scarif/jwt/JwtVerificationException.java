package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtVerificationException extends Exception {
    public JwtVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO подумать над надобностью этого, по сути же ничего не меняется. Может просто отлавливать
    //  исходную ошибку? Или дело в разном смысле логирования, что-то норм а что-то - Security issue
    public static DecodedJWT verify(JWTVerifier verifier, String token) throws JwtVerificationException {
        try {
            // TODO тесты на все виды ошибок
            // воссоздать все случаи
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            // TODO log.info или debug (включить в лог саму ошибку)
            //  trace!
            throw new JwtVerificationException("Токен просрочен", e);
        } catch (SignatureVerificationException e) {
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            throw new JwtVerificationException("Подпись JWT неверна", e);
        } catch (AlgorithmMismatchException e) {
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            throw new JwtVerificationException("Алгоритм подписи не совпадает", e);
        } catch (JWTDecodeException e) {
            // TODO log.warn попытка взлома? (включить в лог саму ошибку)
            throw new JwtVerificationException("Некорректный формат JWT", e);
        } catch (Exception e) {
            // TODO log.error (unknown error) (включить в лог саму ошибку)
            throw new JwtVerificationException("Неизвестная ошибка верификации JWT", e);
        }
    }
}
