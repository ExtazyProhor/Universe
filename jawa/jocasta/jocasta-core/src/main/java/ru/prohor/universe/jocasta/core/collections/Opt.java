package ru.prohor.universe.jocasta.core.collections;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableSupplier;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public abstract class Opt<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 204194056296233266L;
    private static final String OPTIONAL_PARAMETER_WARNING = "OptionalUsedAsFieldOrParameterType";
    private static final String UNCHECKED_WARNING = "unchecked";

    private Opt() {}

    public final boolean isEmpty() {
        return !isPresent();
    }

    public final boolean isPresent() {
        return this instanceof Some;
    }

    public final boolean isSame(T value) {
        return isPresent() && Objects.equals(get(), value);
    }

    public final boolean isMatch(Predicate<T> predicate) {
        return isPresent() && predicate.test(get());
    }

    @Nonnull
    public abstract T get() throws NoSuchElementException;

    public T orElse(T other) {
        return isPresent() ? get() : other;
    }

    @Nullable
    public T orElseNull() {
        return isPresent() ? get() : null;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? get() : supplier.get();
    }

    public T orElseThrow() {
        if (isPresent())
            return get();
        throw new NoSuchElementException("No value present");
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent())
            return get();
        throw exceptionSupplier.get();
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public final Opt<T> orElse(Opt<? extends T> other) {
        return isPresent() ? this : (Opt<T>) other;
    }


    public final Opt<T> orElse(Supplier<Opt<T>> supplier) {
        return isPresent() ? this : supplier.get();
    }

    public abstract <U> Opt<U> map(Function<? super T, U> f);

    public Optional<T> unwrap() {
        return isPresent() ? Optional.of(get()) : Optional.empty();
    }

    public OptionalInt unwrap(ToIntFunction<T> f) {
        return isPresent() ? OptionalInt.of(f.applyAsInt(get())) : OptionalInt.empty();
    }

    public OptionalDouble unwrap(ToDoubleFunction<T> f) {
        return isPresent() ? OptionalDouble.of(f.applyAsDouble(get())) : OptionalDouble.empty();
    }

    public OptionalLong unwrap(ToLongFunction<T> f) {
        return isPresent() ? OptionalLong.of(f.applyAsLong(get())) : OptionalLong.empty();
    }

    public final <U> Opt<U> flatMapO(Function<? super T, Opt<U>> f) {
        return isPresent() ? f.apply(get()) : empty();
    }

    public <B> Opt<B> filterMapOptional(Function<? super T, Optional<B>> f) {
        return flatMapO(e -> f.apply(e).map(Opt::of).orElse(empty()));
    }

    public <B> List<B> flatMap(Function<? super T, ? extends List<B>> f) {
        return isPresent() ? f.apply(get()) : List.of();
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public Opt<T> or(@Nonnull Supplier<? extends Opt<? extends T>> supplier) {
        return isPresent() ? this : (Opt<T>) supplier.get();
    }

    public Opt<T> filter(@Nonnull Predicate<? super T> predicate) {
        return isPresent() && !predicate.test(get()) ? empty() : this;
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public <F> Opt<F> flattenO() {
        return flatMapO(o -> (Opt<F>) o);
    }


    public final Set<T> toSet() {
        return isPresent() ? Set.of(get()) : Set.of();
    }

    public final List<T> toList() {
        return isPresent() ? List.of(get()) : List.of();
    }

    public Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.empty();
    }

    public Set<T> unique() {
        return toSet();
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public static <B, A extends B> Opt<A> safeCast(Opt<B> o) {
        return (Opt<A>) o;
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public <F> Opt<F> cast() {
        return (Opt<F>) this;
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public <F> Opt<F> castOrEmpty() {
        try {
            return (Opt<F>) this;
        } catch (ClassCastException e) {
            return empty();
        }
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent())
            consumer.accept(get());
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (isPresent())
            action.accept(get());
        else
            emptyAction.run();
    }

    @SuppressWarnings(UNCHECKED_WARNING)
    public static <T> Opt<T> empty() {
        return (Opt<T>) Empty.EMPTY;
    }

    public static <T> Opt<T> of(@Nonnull T x) {
        return new Some<>(x);
    }

    public static <T> Opt<T> ofNullable(@Nullable T x) {
        return x == null ? empty() : of(x);
    }

    public static <T> Opt<T> tryOrNull(ThrowableSupplier<T> supplier) {
        try {
            return ofNullable(supplier.get());
        } catch (Exception e) {
            return empty();
        }
    }

    public static <T> Opt<T> when(boolean predicate, @Nonnull T x) {
        return predicate ? of(x) : empty();
    }

    public static <T> Opt<T> when(boolean predicate, Supplier<T> supplier) {
        return predicate ? of(supplier.get()) : empty();
    }

    @SuppressWarnings(OPTIONAL_PARAMETER_WARNING)
    public static <T> Opt<T> wrap(Optional<T> optional) {
        return optional.map(Opt::of).orElseGet(Opt::empty);
    }

    @SuppressWarnings(OPTIONAL_PARAMETER_WARNING)
    public static Opt<Integer> wrap(OptionalInt optional) {
        return optional.isPresent() ? of(optional.getAsInt()) : empty();
    }

    @SuppressWarnings(OPTIONAL_PARAMETER_WARNING)
    public static Opt<Long> wrap(OptionalLong optional) {
        return optional.isPresent() ? of(optional.getAsLong()) : empty();
    }


    @SuppressWarnings(OPTIONAL_PARAMETER_WARNING)
    public static Opt<Double> wrap(OptionalDouble optional) {
        return optional.isPresent() ? of(optional.getAsDouble()) : empty();
    }

    public Object[] toArray() {
        return map(t -> new Object[]{t}).orElse(Empty.EMPTY_ARRAY);
    }

    public static final class Some<T> extends Opt<T> {
        @Serial
        private static final long serialVersionUID = 5105612001846491735L;

        private final T value;

        private Some(@Nonnull T value) {
            this.value = value;
        }

        @Override
        @Nonnull
        public T get() {
            return value;
        }

        @Override
        public <U> Opt<U> map(Function<? super T, U> f) {
            return of(f.apply(value));
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Some))
                return false;
            return Objects.equals(this.value, ((Some<?>) obj).value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    public static final class Empty<T> extends Opt<T> {
        @Serial
        private static final long serialVersionUID = 120651923649551207L;
        private static final Object[] EMPTY_ARRAY = new Object[0];

        @SuppressWarnings("rawtypes")
        private static final Empty EMPTY = new Empty();

        private Empty() {}

        @Override
        @Nonnull
        public T get() {
            throw new NoSuchElementException();
        }

        @Override
        @SuppressWarnings(UNCHECKED_WARNING)
        public <U> Opt<U> map(Function<? super T, U> f) {
            return (Opt<U>) this;
        }

        @Override
        public String toString() {
            return "Empty";
        }

        @Override
        public int hashCode() {
            return 0xB6D890F7;
        }
    }
}
