package ru.prohor.universe.jocasta.core.features.benchmark;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableRunnable;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableSupplier;
import ru.prohor.universe.jocasta.core.functional.NilFunction;

public class Benchmark {
    private static final int NANOS_IN_MILLI = 1_000_000;

    private Benchmark() {}

    /**
     * Measures the execution time of a given task
     *
     * @param task task for execution
     * @return execution time in milliseconds
     */
    public static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / NANOS_IN_MILLI;
    }

    /**
     * Measures the execution time of a given task
     *
     * @param task task for execution
     * @return execution time in milliseconds
     */
    public static long measureSneaky(ThrowableRunnable task) {
        long start = System.nanoTime();
        Sneaky.execute(task);
        return (System.nanoTime() - start) / NANOS_IN_MILLI;
    }

    /**
     * Measures the execution time of a given supplier
     *
     * @param supplier task for execution with return value
     * @return benchmark result with execution time and return value of supplier
     */
    public static <T> BenchmarkResult<T> measure(NilFunction<T> supplier) {
        long start = System.nanoTime();
        T result = supplier.apply();
        long elapsed = (System.nanoTime() - start) / NANOS_IN_MILLI;
        return new BenchmarkResult<>(result, elapsed);
    }

    /**
     * Measures the execution time of a given supplier
     *
     * @param supplier task for execution with return value
     * @return benchmark result with execution time and return value of supplier
     */
    public static <T> BenchmarkResult<T> measureSneaky(ThrowableSupplier<T> supplier) {
        long start = System.nanoTime();
        T result = Sneaky.execute(supplier);
        long elapsed = (System.nanoTime() - start) / NANOS_IN_MILLI;
        return new BenchmarkResult<>(result, elapsed);
    }
}
