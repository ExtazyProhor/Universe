package ru.prohor.universe.jocasta.spring;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.Arrays;

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
}
