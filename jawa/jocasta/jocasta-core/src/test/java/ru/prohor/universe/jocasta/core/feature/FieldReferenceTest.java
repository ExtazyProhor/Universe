package ru.prohor.universe.jocasta.core.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.features.fieldref.InnerFieldReference;
import ru.prohor.universe.jocasta.core.features.fieldref.Name;

public class FieldReferenceTest {
    private record OuterClass(
            String someField,
            int count,
            DataInfoClass info
    ) {}

    private record DataInfoClass(
            MyInnerClass inner,
            boolean isActive
    ) {}

    private record MyInnerClass(
            @Name("collection_name")
            String collectionName
    ) {}

    @Test
    public void testInnerFieldRef() {
        String expected = "info.inner.collection_name";
        String actual = InnerFieldReference
                .create(OuterClass::info)
                .then(DataInfoClass::inner)
                .then(MyInnerClass::collectionName)
                .name();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testInnerFieldRefWithCustomDelimiter() {
        String expected = "info/inner/collection_name";
        String actual = InnerFieldReference
                .create(OuterClass::info, "/")
                .then(DataInfoClass::inner)
                .then(MyInnerClass::collectionName)
                .name();
        Assertions.assertEquals(expected, actual);
    }
}
