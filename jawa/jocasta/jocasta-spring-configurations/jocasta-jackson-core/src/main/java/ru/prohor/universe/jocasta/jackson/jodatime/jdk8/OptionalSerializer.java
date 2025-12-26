package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class OptionalSerializer extends ReferenceTypeSerializer<Optional<?>> {
    public OptionalSerializer(
            ReferenceType fullType,
            boolean staticTyping,
            TypeSerializer typeSerializer,
            JsonSerializer<Object> serializer
    ) {
        super(fullType, staticTyping, typeSerializer, serializer);
    }

    private OptionalSerializer(
            OptionalSerializer base,
            BeanProperty property,
            TypeSerializer typeSerializer,
            JsonSerializer<?> serializer,
            NameTransformer unwrapper,
            Object suppressableValue,
            boolean suppressNulls
    ) {
        super(base, property, typeSerializer, serializer, unwrapper, suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<Optional<?>> withResolved(
            BeanProperty property,
            TypeSerializer typeSerializer,
            JsonSerializer<?> serializer,
            NameTransformer unwrapper
    ) {
        return new OptionalSerializer(
                this,
                property,
                typeSerializer,
                serializer,
                unwrapper,
                _suppressableValue,
                _suppressNulls
        );
    }

    @Override
    public ReferenceTypeSerializer<Optional<?>> withContentInclusion(
            Object suppressableValue,
            boolean suppressNulls
    ) {
        return new OptionalSerializer(
                this,
                _property,
                _valueTypeSerializer,
                _valueSerializer,
                _unwrapper,
                suppressableValue,
                suppressNulls
        );
    }

    @Override
    protected boolean _isValuePresent(Optional<?> value) {
        return value.isPresent();
    }

    @Override
    protected Object _getReferenced(Optional<?> value) {
        assert value.isPresent(); // TODO remove
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(Optional<?> value) {
        return value.orElse(null);
    }
}
