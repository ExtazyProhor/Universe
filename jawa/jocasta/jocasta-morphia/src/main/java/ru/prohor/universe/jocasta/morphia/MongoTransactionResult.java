package ru.prohor.universe.jocasta.morphia;

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
}
