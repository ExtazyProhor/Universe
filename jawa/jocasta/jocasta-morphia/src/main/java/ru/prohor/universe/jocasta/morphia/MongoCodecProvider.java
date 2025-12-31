package ru.prohor.universe.jocasta.morphia;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MongoCodecProvider implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs;

    public MongoCodecProvider(List<Codec<?>> codecs) {
        this.codecs = codecs.stream().collect(Collectors.toMap(Codec::getEncoderClass, Function.identity()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return Opt.ofNullable(codecs.get(clazz)).map(codec -> (Codec<T>) codec).orElseNull();
    }
}
