package ru.prohor.universe.jocasta.jackson.jodatime.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class OptDeserializer extends ReferenceTypeDeserializer<Opt<?>> {
    private final static boolean DEFAULT_READ_ABSENT_AS_NULL = false;

    public OptDeserializer(
            JavaType fullType,
            ValueInstantiator instantiator,
            TypeDeserializer typeDeserializer,
            JsonDeserializer<?> deserializer
    ) {
        super(fullType, instantiator, typeDeserializer, deserializer);
    }

    @Override
    public OptDeserializer withResolved(TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
        return new OptDeserializer(
                _fullType,
                _valueInstantiator,
                typeDeserializer,
                deserializer
        );
    }

    @Override
    public Opt<?> getNullValue(DeserializationContext context) throws JsonMappingException {
        return Opt.ofNullable(_valueDeserializer.getNullValue(context));
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
    public Opt<?> referenceValue(Object contents) {
        return Opt.ofNullable(contents);
    }

    @Override
    public Object getReferenced(Opt<?> reference) {
        return reference.orElseNull();
    }

    @Override
    public Opt<?> updateReference(Opt<?> reference, Object contents) {
        return Opt.ofNullable(contents);
    }
}
