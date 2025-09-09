package ru.prohor.universe.jocasta.core.features.sneaky;

public class Sneaky {
    private Sneaky() {}

    public static <T> T wrap(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throwUnchecked(e);
            return null;
        }
    }

    public static void wrap(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throwUnchecked(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Exception> void throwUnchecked(Exception e) throws T {
        throw (T) e;
    }
}
