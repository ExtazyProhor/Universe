package ru.prohor.universe.jocasta.core.features.sqlmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking class fields as attributes of an SQL entity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityField {
    /**
     * @return attribute name
     */
    String name();
}
