package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class OptionalDeserializer extends ReferenceTypeDeserializer<Optional<?>> {
    private final static boolean DEFAULT_READ_ABSENT_AS_NULL = false;

    public OptionalDeserializer(
            JavaType fullType,
            ValueInstantiator instantiator,
            TypeDeserializer typeDeserializer,
            JsonDeserializer<?> deserializer
    ) {
        super(fullType, instantiator, typeDeserializer, deserializer);
    }

    @Override
    public OptionalDeserializer withResolved(TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
        return new OptionalDeserializer(
                _fullType,
                _valueInstantiator,
                typeDeserializer,
                deserializer
        );
    }

    @Override
    public Optional<?> getNullValue(DeserializationContext context) throws JsonMappingException {
        return Optional.ofNullable(_valueDeserializer.getNullValue(context));
    }

    @Override
    public Object getEmptyValue(DeserializationContext context) throws JsonMappingException {
        return getNullValue(context);
    }

    @Override
    public Object getAbsentValue(DeserializationContext context) throws JsonMappingException {
        if (DEFAULT_READ_ABSENT_AS_NULL)
            return null;
        return getNullValue(context);
    }

    @Override
    public Optional<?> referenceValue(Object contents) {
        return Optional.ofNullable(contents);
    }

    @Override
    public Object getReferenced(Optional<?> reference) {
        return reference.orElse(null);
    }

    @Override
    public Optional<?> updateReference(Optional<?> reference, Object contents) {
        return Optional.ofNullable(contents);
    }
}
