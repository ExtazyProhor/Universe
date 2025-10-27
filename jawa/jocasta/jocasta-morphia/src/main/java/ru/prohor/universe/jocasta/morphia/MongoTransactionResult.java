package ru.prohor.universe.jocasta.morphia;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class MongoTransactionResult<T> {
    public final boolean success;
    public final T value;

    private MongoTransactionResult(boolean success, T value) {
        this.success = success;
        this.value = value;
    }

    public static <T> MongoTransactionResult<T> success(T value) {
        return new MongoTransactionResult<>(true, value);
    }

    public static <T> MongoTransactionResult<T> error() {
        return new MongoTransactionResult<>(false, null);
    }

    public Opt<T> asOpt() {
        return Opt.when(success, value);
    }
}
