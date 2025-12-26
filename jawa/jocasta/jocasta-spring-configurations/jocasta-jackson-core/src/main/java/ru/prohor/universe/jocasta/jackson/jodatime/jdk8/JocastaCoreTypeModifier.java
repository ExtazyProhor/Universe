package ru.prohor.universe.jocasta.jackson.jodatime.jdk8;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public class JocastaCoreTypeModifier extends TypeModifier {
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory) {
        if (type.isReferenceType() || type.isContainerType())
            return type;
        Class<?> raw = type.getRawClass();
        if (raw != Opt.class)
            return type;

        JavaType refType = type.containedTypeOrUnknown(0);
        return ReferenceType.upgradeFrom(type, refType);
    }
}
