package ru.prohor.universe.jocasta.jackson.core.deserializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class JocastaCoreDeserializers extends Deserializers.Base {
    @Override
    public JsonDeserializer<?> findReferenceDeserializer(
            ReferenceType refType,
            DeserializationConfig config,
            BeanDescription beanDescription,
            TypeDeserializer contentTypeDeserializer,
            JsonDeserializer<?> contentDeserializer
    ) {
        if (!refType.hasRawClass(Opt.class))
            return null;
        return new OptDeserializer(
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
        if (!type.hasRawClass(Opt.class))
            return null;
        JavaType refType = config.constructType(Opt.class);
        return new OptDeserializer(refType, null, null, null);
    }
}
