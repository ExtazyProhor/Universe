package ru.prohor.universe.jocasta.core.features.fieldref;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class FieldReferenceTest {
    private record Person(
            String firstName,
            @Name("surname")
            String lastName,
            int age
    ) {}

    private record WithExtraMethod(String field) {
        public String notAField() {
            return "error";
        }
    }

    private static class NotARecord {
        private final String field;

        public NotARecord() {
            this.field = "value";
        }

        public String field() {
            return field;
        }
    }

    private static <T> String name(FieldReference<T> ref) {
        return ref.name();
    }

    @Test
    void testFieldWithoutNameAnnotation() {
        String result = name(Person::firstName);
        Assertions.assertEquals("firstName", result);
    }

    @Test
    void testFieldWithNameAnnotation() {
        String result = name(Person::lastName);
        Assertions.assertEquals("surname", result);
    }

    @Test
    void testFieldWithoutAnnotationForPrimitive() {
        String result = name(Person::age);
        Assertions.assertEquals("age", result);
    }

    @Test
    void testThrowsExceptionWhenNotRecord() {
        Executable executable = () -> name(NotARecord::field);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, executable);

        Assertions.assertTrue(exception.getMessage().contains("Error when calculating field name"));
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        Assertions.assertTrue(exception.getCause().getMessage().contains("record method"));
    }

    @Test
    void testThrowsExceptionWhenNotRecordComponent() {
        Executable executable = () -> name(WithExtraMethod::notAField);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, executable);

        Assertions.assertTrue(exception.getMessage().contains("Error when calculating field name"));
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        Assertions.assertTrue(exception.getCause().getMessage().contains("record component"));
    }

    @Test
    void testThrowsExceptionWhenReflectionFails() {
        FieldReference<Person> ref = source -> null;
        Executable executable = ref::name;
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                executable
        );
        Assertions.assertTrue(
                exception.getMessage().contains("Error when calculating field name"),
                "Expected generic reflection error message"
        );
    }

    @Test
    void testGetMethodInvocation() {
        Person person = new Person("John", "Doe", 30);
        FieldReference<Person> ref = Person::firstName;
        Object value = ref.get(person);
        Assertions.assertEquals("John", value);
    }
}
