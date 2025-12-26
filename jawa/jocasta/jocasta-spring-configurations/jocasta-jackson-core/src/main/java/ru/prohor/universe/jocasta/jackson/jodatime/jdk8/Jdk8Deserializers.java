package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;

import java.util.Optional;

public class Jdk8Deserializers extends Deserializers.Base {
    @Override
    public JsonDeserializer<?> findReferenceDeserializer(
            ReferenceType refType,
            DeserializationConfig config,
            BeanDescription beanDescription,
            TypeDeserializer contentTypeDeserializer,
            JsonDeserializer<?> contentDeserializer
    ) {
        if (!refType.hasRawClass(Optional.class))
            return null;
        return new OptionalDeserializer(
                refType,
                null,
                contentTypeDeserializer,
                contentDeserializer
        );
    }

    @Override
    public JsonDeserializer<?> findBeanDeserializer(
            JavaType type,
            DeserializationConfig config,
            BeanDescription beanDesc
    ) {
        if (!type.hasRawClass(Optional.class))
            return null;
        JavaType refType = config.constructType(Optional.class);
        return new OptionalDeserializer(refType, null, null, null);
    }
}
