package ru.prohor.universe.jocasta.core.utils;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtils {
    private static String getStackTraceAsStringInternal(Throwable throwable) throws Exception {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

    public static String getStackTraceAsString(Throwable throwable) {
        return Sneaky.execute(() -> getStackTraceAsStringInternal(throwable));
    }
}
