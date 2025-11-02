package ru.prohor.universe.jocasta.core.collections.common;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import ru.prohor.universe.jocasta.core.features.sneaky.ThrowableSupplier;
import ru.prohor.universe.jocasta.core.functional.MonoConsumer;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;
import ru.prohor.universe.jocasta.core.functional.Supplier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
 * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
 * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
 */
public abstract class Opt<T> {
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

    public final boolean isMatch(MonoPredicate<T> predicate) {
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

    @SuppressWarnings("unchecked")
    public final Opt<T> orElse(Opt<? extends T> other) {
        return isPresent() ? this : (Opt<T>) other;
    }


    public final Opt<T> orElse(Supplier<Opt<T>> supplier) {
        return isPresent() ? this : supplier.get();
    }

    public abstract <U> Opt<U> map(MonoFunction<? super T, U> f);

    public Optional<T> unwrap() {
        return isPresent() ? Optional.of(get()) : Optional.empty();
    }

    public OptionalInt unwrapAsInt(MonoFunction<T, Integer> f) {
        return isPresent() ? OptionalInt.of(f.apply(get())) : OptionalInt.empty();
    }

    public OptionalDouble unwrapAsDouble(MonoFunction<T, Double> f) {
        return isPresent() ? OptionalDouble.of(f.apply(get())) : OptionalDouble.empty();
    }

    public OptionalLong unwrapAsLong(MonoFunction<T, Long> f) {
        return isPresent() ? OptionalLong.of(f.apply(get())) : OptionalLong.empty();
    }

    public final <U> Opt<U> flatMapO(MonoFunction<? super T, Opt<U>> f) {
        return isPresent() ? f.apply(get()) : empty();
    }

    public <B> Opt<B> filterMapOptional(MonoFunction<? super T, Optional<B>> f) {
        return flatMapO(e -> f.apply(e).map(Opt::of).orElse(empty()));
    }

    public <B> List<B> flatMap(MonoFunction<? super T, ? extends List<B>> f) {
        return isPresent() ? f.apply(get()) : List.of();
    }

    @SuppressWarnings("unchecked")
    public Opt<T> or(@Nonnull Supplier<? extends Opt<? extends T>> supplier) {
        return isPresent() ? this : (Opt<T>) supplier.get();
    }

    public Opt<T> filter(@Nonnull MonoPredicate<? super T> predicate) {
        return isPresent() && !predicate.test(get()) ? empty() : this;
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public static <B, A extends B> Opt<A> safeCast(Opt<B> o) {
        return (Opt<A>) o;
    }

    @SuppressWarnings("unchecked")
    public <F> Opt<F> cast() {
        return (Opt<F>) this;
    }

    @SuppressWarnings("unchecked")
    public <F> Opt<F> castOrEmpty() {
        try {
            return (Opt<F>) this;
        } catch (ClassCastException e) {
            return empty();
        }
    }

    public void ifPresent(MonoConsumer<? super T> consumer) {
        if (isPresent())
            consumer.accept(get());
    }

    public void ifPresentOrElse(MonoConsumer<? super T> action, Runnable emptyAction) {
        if (isPresent())
            action.accept(get());
        else
            emptyAction.run();
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Opt<T> wrap(Optional<T> optional) {
        return optional.map(Opt::of).orElseGet(Opt::empty);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Opt<Integer> wrap(OptionalInt optional) {
        return optional.isPresent() ? of(optional.getAsInt()) : empty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Opt<Long> wrap(OptionalLong optional) {
        return optional.isPresent() ? of(optional.getAsLong()) : empty();
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Opt<Double> wrap(OptionalDouble optional) {
        return optional.isPresent() ? of(optional.getAsDouble()) : empty();
    }

    public Object[] toArray() {
        return map(t -> new Object[]{t}).orElse(Empty.EMPTY_ARRAY);
    }

    public static final class Some<T> extends Opt<T> {
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
        public <U> Opt<U> map(MonoFunction<? super T, U> f) {
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
        @SuppressWarnings("unchecked")
        public <U> Opt<U> map(MonoFunction<? super T, U> f) {
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
