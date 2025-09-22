package ru.prohor.universe.jocasta.springweb;

import jakarta.servlet.http.HttpServletRequest;

public class ControllersUtils {
    private ControllersUtils() {}

    public static String getPathWithoutSlashes(HttpServletRequest request) {
        return removeSlashes(request.getRequestURI().substring(request.getContextPath().length()));
    }

    private static String removeSlashes(String s) {
        if (s.endsWith("/"))
            s = s.substring(0, s.length() - 1);
        if (s.startsWith("/"))
            s = s.substring(1);
        return s;
    }
}
