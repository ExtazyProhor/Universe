package ru.prohor.universe.jocasta.jackson.jodatime.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class JocastaCoreSerializers extends Serializers.Base {
    @Override
    public JsonSerializer<?> findReferenceSerializer(
            SerializationConfig config,
            ReferenceType refType,
            BeanDescription beanDescription,
            TypeSerializer contentTypeSerializer,
            JsonSerializer<Object> contentValueSerializer
    ) {
        Class<?> raw = refType.getRawClass();
        if (!Opt.class.isAssignableFrom(raw))
            return null;
        boolean staticTyping = (contentTypeSerializer == null) && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
        return new OptSerializer(
                refType,
                staticTyping,
                contentTypeSerializer,
                contentValueSerializer
        );
    }
}
