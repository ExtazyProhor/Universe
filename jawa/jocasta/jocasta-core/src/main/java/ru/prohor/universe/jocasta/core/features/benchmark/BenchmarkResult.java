package ru.prohor.universe.jocasta.core.features.benchmark;

/**
 * @param result        result
 * @param executionTime execution time in milliseconds
 * @param <T>           type of result
 */
public record BenchmarkResult<T>(
        T result,
        long executionTime
) {}
