package ru.prohor.universe.jocasta.core.features.sneaky;

import java.util.function.Supplier;

public class Sneaky {
    private Sneaky() {}

    public static <T> T execute(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throwUnchecked(e);
            throw new RuntimeException();
        }
    }

    public static void execute(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throwUnchecked(e);
        }
    }

    public static <T> Supplier<T> wrap(ThrowableSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throwUnchecked(e);
                throw new RuntimeException();
            }
        };
    }

    public static Runnable wrap(ThrowableRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throwUnchecked(e);
            }
        };
    }

    public static void silent(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    public static <T extends Exception> void throwUnchecked(Exception e) throws T {
        throw (T) e;
    }
}
