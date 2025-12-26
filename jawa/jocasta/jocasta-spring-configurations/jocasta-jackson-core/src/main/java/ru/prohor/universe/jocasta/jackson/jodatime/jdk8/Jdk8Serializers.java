package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;

import java.util.Optional;

public class Jdk8Serializers extends Serializers.Base {
    @Override
    public JsonSerializer<?> findReferenceSerializer(
            SerializationConfig config,
            ReferenceType refType,
            BeanDescription beanDescription,
            TypeSerializer contentTypeSerializer,
            JsonSerializer<Object> contentValueSerializer
    ) {
        Class<?> raw = refType.getRawClass();
        if (!Optional.class.isAssignableFrom(raw))
            return null;
        boolean staticTyping = (contentTypeSerializer == null) && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
        return new OptionalSerializer(
                refType,
                staticTyping,
                contentTypeSerializer,
                contentValueSerializer
        );
    }
}
