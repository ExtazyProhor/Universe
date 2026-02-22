package ru.prohor.universe.jocasta.core.features.fieldref;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;

import java.util.ArrayList;
import java.util.List;

public class FieldReferenceChain<T, R> implements FieldProperties<T, R> {
    private final MonoFunction<T, Opt<R>> mapper;
    private final List<String> nameParts;

    public FieldReferenceChain(MonoFunction<T, Opt<R>> mapper, List<String> nameParts) {
        this.mapper = mapper;
        this.nameParts = nameParts;
    }

    static <T, R> FieldReferenceChain<T, R> create(FieldRef<T, R> fieldRef) {
        List<String> nameParts = new ArrayList<>();
        nameParts.add(fieldRef.name());
        return new FieldReferenceChain<>(fieldRef::getO, nameParts);
    }

    /**
     * After calling the method, the old object cannot be used
     */
    public <P> FieldReferenceChain<T, P> then(FieldReference<R, P> fieldReference) {
        this.nameParts.add(fieldReference.name());
        MonoFunction<T, Opt<P>> mapper = t -> this.mapper.apply(t).map(fieldReference::get);
        return new FieldReferenceChain<>(mapper, this.nameParts);
    }

    /**
     * After calling the method, the old object cannot be used
     */
    public <P> FieldReferenceChain<T, P> thenO(OptFieldReference<R, P> optFieldReference) {
        this.nameParts.add(optFieldReference.name());
        MonoFunction<T, Opt<P>> mapper = t -> this.mapper.apply(t).map(optFieldReference::getO).flattenO();
        return new FieldReferenceChain<>(mapper, this.nameParts);
    }

    @Override
    public Opt<R> getO(T source) {
        return mapper.apply(source);
    }

    @Override
    public String name(String delimiter) {
        return String.join(delimiter, nameParts);
    }
}
