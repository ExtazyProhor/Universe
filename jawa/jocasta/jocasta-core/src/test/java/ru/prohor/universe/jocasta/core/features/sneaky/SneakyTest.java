package ru.prohor.universe.jocasta.core.features.sneaky;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SneakyTest {
    @Test
    void testSupplierReturnsValue() {
        String result = Sneaky.execute(() -> "Hello");
        Assertions.assertEquals("Hello", result);
    }

    @Test
    void testSupplierThrowsCheckedException() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> Sneaky.execute(() -> {
                    throw new Exception("Checked!");
                })
        );
        Assertions.assertEquals("Checked!", exception.getMessage());
    }

    @Test
    void testRunnableRunsSuccessfully() {
        AtomicBoolean called = new AtomicBoolean(false);
        Sneaky.execute(() -> called.set(true));
        Assertions.assertTrue(called.get());
    }

    @Test
    void testRunnableThrowsCheckedException() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> Sneaky.execute((ThrowableRunnable) () -> {
                    throw new Exception("Boom");
                })
        );
        Assertions.assertEquals("Boom", exception.getMessage());
    }

    @Test
    void testWrapSupplierReturnsSameValue() {
        Supplier<String> supplier = Sneaky.wrap(() -> "wrapped");
        Assertions.assertEquals("wrapped", supplier.get());
    }

    @Test
    void testWrapSupplierThrowsUnchecked() {
        Supplier<String> supplier = Sneaky.wrap(() -> {
            throw new Exception("fail");
        });
        Exception exception = Assertions.assertThrows(Exception.class, supplier::get);
        Assertions.assertEquals("fail", exception.getMessage());
    }

    @Test
    void testWrapRunnableRunsSuccessfully() {
        AtomicBoolean flag = new AtomicBoolean(false);
        Runnable runnable = Sneaky.wrap(() -> flag.set(true));
        runnable.run();
        Assertions.assertTrue(flag.get());
    }

    @Test
    void testWrapRunnableThrowsUnchecked() {
        Runnable runnable = Sneaky.wrap((ThrowableRunnable) () -> {
            throw new Exception("run error");
        });
        Exception exception = Assertions.assertThrows(Exception.class, runnable::run);
        Assertions.assertEquals("run error", exception.getMessage());
    }

    @Test
    void testSilentSuppressesException() {
        AtomicBoolean executed = new AtomicBoolean(false);
        Sneaky.silent(() -> {
            executed.set(true);
            throw new Exception("ignore me");
        });
        Assertions.assertTrue(executed.get());
    }

    @Test
    void testThrowUncheckedReThrowsException() {
        Exception ex = new Exception("unchecked");
        Exception thrown = Assertions.assertThrows(Exception.class, () -> Sneaky.throwUnchecked(ex));
        Assertions.assertEquals("unchecked", thrown.getMessage());
    }
}
