package ru.prohor.universe.jocasta.core.collections.common;

import jakarta.annotation.Nonnull;

import java.util.function.Function;

public record Result<T>(
        T result,
        String error
) {
    public static <T> Result<T> of(T result) {
        return new Result<>(result, null);
    }

    public static <T> Result<T> error(@Nonnull String message) {
        return new Result<>(null, message);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isError() {
        return !isSuccess();
    }

    public <U> Result<U> map(Function<T, U> remappingFunction) {
        if (isError())
            return new Result<>(null, error);
        return new Result<>(remappingFunction.apply(result), null);
    }

    public <U> U mapOrElse(Function<T, U> remappingFunction, Function<String, U> errorMappingFunction) {
        if (isSuccess())
            return remappingFunction.apply(result);
        return errorMappingFunction.apply(error);
    }

    // TODO methods from Opt<>
}
