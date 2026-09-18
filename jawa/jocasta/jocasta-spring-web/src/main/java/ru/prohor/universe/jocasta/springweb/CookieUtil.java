package ru.prohor.universe.jocasta.springweb;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CookieUtil {
    public static Opt<String> getCookieValue(HttpServletRequest request, String cookieName) {
        return Opt.ofNullable(request.getCookies()).flatMapO(
                cookies -> Opt.wrap(
                        Arrays.stream(cookies)
                                .filter(cookie -> cookieName.equals(cookie.getName()))
                                .map(Cookie::getValue)
                                .findAny()
                )
        );
    }

    public static HttpHeaders setCookieHeader(String cookie) {
        return new HttpHeaders(MultiValueMap.fromMultiValue(Map.of(
                HttpHeaders.SET_COOKIE,
                List.of(cookie)
        )));
    }
}
