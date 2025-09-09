package ru.prohor.universe.jocasta.core.features.sneaky;

public class Sneaky {
    private Sneaky() {}

    public static <T> T wrap(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void wrap(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
