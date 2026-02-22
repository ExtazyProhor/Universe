package ru.prohor.universe.jocasta.jackson.core.serializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class OptSerializer extends ReferenceTypeSerializer<Opt<?>> {
    public OptSerializer(
            ReferenceType fullType,
            boolean staticTyping,
            TypeSerializer typeSerializer,
            JsonSerializer<Object> serializer
    ) {
        super(fullType, staticTyping, typeSerializer, serializer);
    }

    private OptSerializer(
            OptSerializer base,
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
    protected ReferenceTypeSerializer<Opt<?>> withResolved(
            BeanProperty property,
            TypeSerializer typeSerializer,
            JsonSerializer<?> serializer,
            NameTransformer unwrapper
    ) {
        return new OptSerializer(
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
    public ReferenceTypeSerializer<Opt<?>> withContentInclusion(
            Object suppressableValue,
            boolean suppressNulls
    ) {
        return new OptSerializer(
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
    protected boolean _isValuePresent(Opt<?> value) {
        return value.isPresent();
    }

    @Override
    protected Object _getReferenced(Opt<?> value) {
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(Opt<?> value) {
        return value.orElseNull();
    }
}
