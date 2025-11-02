package ru.prohor.universe.jocasta.core.collections.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

class OptTest {
    @Nested
    class CreationTests {
        @Test
        void testOfCreatesNonEmpty() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void testOfWithNull() {
            Assertions.assertThrows(NullPointerException.class, () -> Opt.of(null));
        }

        @Test
        void testEmpty() {
            Opt<String> opt = Opt.empty();
            Assertions.assertFalse(opt.isPresent());
            Assertions.assertTrue(opt.isEmpty());
        }

        @Test
        void testOfNullableWithValue() {
            Opt<String> opt = Opt.ofNullable("test");
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testOfNullableWithNull() {
            Opt<String> opt = Opt.ofNullable(null);
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testWhenTrue() {
            Opt<String> opt = Opt.when(true, "test");
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testWhenFalse() {
            Opt<String> opt = Opt.when(false, "test");
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testWhenSupplierTrue() {
            Opt<String> opt = Opt.when(true, () -> "test");
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testWhenSupplierFalse() {
            Opt<String> opt = Opt.when(false, () -> "test");
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testTryOrNullSuccess() {
            Opt<String> opt = Opt.tryOrNull(() -> "test");
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testTryOrNullException() {
            Opt<String> opt = Opt.tryOrNull(() -> {
                throw new RuntimeException("error");
            });
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testTryOrNullReturnsNull() {
            Opt<String> opt = Opt.tryOrNull(() -> null);
            Assertions.assertFalse(opt.isPresent());
        }
    }

    @Nested
    class WrapperTests {
        @Test
        void testWrapOptionalPresent() {
            Opt<String> opt = Opt.wrap(Optional.of("test"));
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testWrapOptionalEmpty() {
            Opt<String> opt = Opt.wrap(Optional.empty());
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testWrapOptionalIntPresent() {
            Opt<Integer> opt = Opt.wrap(OptionalInt.of(42));
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(42, opt.get());
        }

        @Test
        void testWrapOptionalIntEmpty() {
            Opt<Integer> opt = Opt.wrap(OptionalInt.empty());
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testWrapOptionalLongPresent() {
            Opt<Long> opt = Opt.wrap(OptionalLong.of(42L));
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(42L, opt.get());
        }

        @Test
        void testWrapOptionalLongEmpty() {
            Opt<Long> opt = Opt.wrap(OptionalLong.empty());
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testWrapOptionalDoublePresent() {
            Opt<Double> opt = Opt.wrap(OptionalDouble.of(42.5));
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(42.5, opt.get());
        }

        @Test
        void testWrapOptionalDoubleEmpty() {
            Opt<Double> opt = Opt.wrap(OptionalDouble.empty());
            Assertions.assertFalse(opt.isPresent());
        }
    }

    @Nested
    class PresenceTests {
        @Test
        void testIsPresentSome() {
            Assertions.assertTrue(Opt.of("test").isPresent());
        }

        @Test
        void testIsPresentEmpty() {
            Assertions.assertFalse(Opt.empty().isPresent());
        }

        @Test
        void testIsEmptySome() {
            Assertions.assertFalse(Opt.of("test").isEmpty());
        }

        @Test
        void testIsEmptyEmpty() {
            Assertions.assertTrue(Opt.empty().isEmpty());
        }
    }

    @Nested
    class GetTests {
        @Test
        void testGetSome() {
            Assertions.assertEquals("test", Opt.of("test").get());
        }

        @Test
        void testGetEmpty() {
            Assertions.assertThrows(NoSuchElementException.class, () -> Opt.empty().get());
        }
    }

    @Nested
    class OrElseTests {
        @Test
        void testOrElseSome() {
            Assertions.assertEquals("test", Opt.of("test").orElse("default"));
        }

        @Test
        void testOrElseEmpty() {
            Assertions.assertEquals("default", Opt.<String>empty().orElse("default"));
        }

        @Test
        void testOrElseNullSome() {
            Assertions.assertEquals("test", Opt.of("test").orElseNull());
        }

        @Test
        void testOrElseNullEmpty() {
            Assertions.assertNull(Opt.<String>empty().orElseNull());
        }

        @Test
        void testOrElseGetSome() {
            Assertions.assertEquals("test", Opt.of("test").orElseGet(() -> "default"));
        }

        @Test
        void testOrElseGetEmpty() {
            Assertions.assertEquals("default", Opt.<String>empty().orElseGet(() -> "default"));
        }

        @Test
        void testOrElseThrowSome() {
            Assertions.assertEquals("test", Opt.of("test").orElseThrow());
        }

        @Test
        void testOrElseThrowEmpty() {
            Assertions.assertThrows(NoSuchElementException.class, () -> Opt.empty().orElseThrow());
        }

        @Test
        void testOrElseThrowSupplierSome() {
            Assertions.assertEquals("test", Opt.of("test").orElseThrow(IllegalStateException::new));
        }

        @Test
        void testOrElseThrowSupplierEmpty() {
            Assertions.assertThrows(IllegalStateException.class,
                    () -> Opt.empty().orElseThrow(IllegalStateException::new));
        }

        @Test
        void testOrElseOptSome() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertSame(opt, opt.orElse(Opt.of("default")));
        }

        @Test
        void testOrElseOptEmpty() {
            Opt<String> other = Opt.of("default");
            Assertions.assertSame(other, Opt.<String>empty().orElse(other));
        }

        @Test
        void testOrElseSupplierOptSome() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertSame(opt, opt.orElse(() -> Opt.of("default")));
        }

        @Test
        void testOrElseSupplierOptEmpty() {
            Opt<String> other = Opt.of("default");
            Assertions.assertSame(other, Opt.<String>empty().orElse(() -> other));
        }

        @Test
        void testOrSome() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertSame(opt, opt.or(() -> Opt.of("default")));
        }

        @Test
        void testOrEmpty() {
            Opt<String> other = Opt.of("default");
            Assertions.assertSame(other, Opt.<String>empty().or(() -> other));
        }
    }

    @Nested
    class ComparisonTests {
        @Test
        void testIsSameTrue() {
            Assertions.assertTrue(Opt.of("test").isSame("test"));
        }

        @Test
        void testIsSameFalse() {
            Assertions.assertFalse(Opt.of("test").isSame("other"));
        }

        @Test
        void testIsSameEmpty() {
            Assertions.assertFalse(Opt.<String>empty().isSame("test"));
        }

        @Test
        void testIsMatchTrue() {
            Assertions.assertTrue(Opt.of("test").isMatch(s -> s.length() == 4));
        }

        @Test
        void testIsMatchFalse() {
            Assertions.assertFalse(Opt.of("test").isMatch(s -> s.length() == 5));
        }

        @Test
        void testIsMatchEmpty() {
            Assertions.assertFalse(Opt.<String>empty().isMatch(s -> true));
        }
    }

    @Nested
    class MapTests {
        @Test
        void testMapSome() {
            Opt<Integer> opt = Opt.of("test").map(String::length);
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(4, opt.get());
        }

        @Test
        void testMapEmpty() {
            Opt<Integer> opt = Opt.<String>empty().map(String::length);
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testMapTypeTransform() {
            Opt<String> opt = Opt.of(42).map(Object::toString);
            Assertions.assertEquals("42", opt.get());
        }
    }

    @Nested
    class FlatMapTests {
        @Test
        void testFlatMapOSomeToSome() {
            Opt<Integer> opt = Opt.of("test").flatMapO(s -> Opt.of(s.length()));
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(4, opt.get());
        }

        @Test
        void testFlatMapOSomeToEmpty() {
            Opt<Integer> opt = Opt.of("test").flatMapO(s -> Opt.empty());
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testFlatMapOEmpty() {
            Opt<Integer> opt = Opt.<String>empty().flatMapO(s -> Opt.of(s.length()));
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testFlatMapSome() {
            List<Character> list = Opt.of("ab").flatMap(s ->
                    s.chars().mapToObj(c -> (char) c).collect(Collectors.toList()));
            Assertions.assertEquals(Arrays.asList('a', 'b'), list);
        }

        @Test
        void testFlatMapEmpty() {
            List<Character> list = Opt.<String>empty().flatMap(s ->
                    s.chars().mapToObj(c -> (char) c).collect(Collectors.toList()));
            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        void testFilterMapOptionalPresent() {
            Opt<Integer> opt = Opt.of("123").filterMapOptional(s -> {
                try {
                    return Optional.of(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals(123, opt.get());
        }

        @Test
        void testFilterMapOptionalEmpty() {
            Opt<Integer> opt = Opt.of("abc").filterMapOptional(s -> {
                try {
                    return Optional.of(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testFlattenO() {
            Opt<Opt<String>> nested = Opt.of(Opt.of("test"));
            Opt<String> flattened = nested.flattenO();
            Assertions.assertTrue(flattened.isPresent());
            Assertions.assertEquals("test", flattened.get());
        }
    }

    @Nested
    class FilterTests {
        @Test
        void testFilterMatch() {
            Opt<String> opt = Opt.of("test").filter(s -> s.length() == 4);
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertEquals("test", opt.get());
        }

        @Test
        void testFilterNoMatch() {
            Opt<String> opt = Opt.of("test").filter(s -> s.length() == 5);
            Assertions.assertFalse(opt.isPresent());
        }

        @Test
        void testFilterEmpty() {
            Opt<String> opt = Opt.<String>empty().filter(s -> true);
            Assertions.assertFalse(opt.isPresent());
        }
    }

    @Nested
    class UnwrapTests {
        @Test
        void testUnwrapSome() {
            Optional<String> optional = Opt.of("test").unwrap();
            Assertions.assertTrue(optional.isPresent());
            Assertions.assertEquals("test", optional.get());
        }

        @Test
        void testUnwrapEmpty() {
            Optional<String> optional = Opt.<String>empty().unwrap();
            Assertions.assertFalse(optional.isPresent());
        }

        @Test
        void testUnwrapAsIntSome() {
            OptionalInt optional = Opt.of("test").unwrapAsInt(String::length);
            Assertions.assertTrue(optional.isPresent());
            Assertions.assertEquals(4, optional.getAsInt());
        }

        @Test
        void testUnwrapAsIntEmpty() {
            OptionalInt optional = Opt.<String>empty().unwrapAsInt(String::length);
            Assertions.assertFalse(optional.isPresent());
        }

        @Test
        void testUnwrapAsLongSome() {
            OptionalLong optional = Opt.of("test").unwrapAsLong(s -> (long) s.length());
            Assertions.assertTrue(optional.isPresent());
            Assertions.assertEquals(4L, optional.getAsLong());
        }

        @Test
        void testUnwrapAsLongEmpty() {
            OptionalLong optional = Opt.<String>empty().unwrapAsLong(s -> (long) s.length());
            Assertions.assertFalse(optional.isPresent());
        }

        @Test
        void testUnwrapAsDoubleSome() {
            OptionalDouble optional = Opt.of("test").unwrapAsDouble(s -> (double) s.length());
            Assertions.assertTrue(optional.isPresent());
            Assertions.assertEquals(4.0, optional.getAsDouble());
        }

        @Test
        void testUnwrapAsDoubleEmpty() {
            OptionalDouble optional = Opt.<String>empty().unwrapAsDouble(s -> (double) s.length());
            Assertions.assertFalse(optional.isPresent());
        }
    }

    @Nested
    class ConversionTests {
        @Test
        void testToSetSome() {
            Set<String> set = Opt.of("test").toSet();
            Assertions.assertEquals(1, set.size());
            Assertions.assertTrue(set.contains("test"));
        }

        @Test
        void testToSetEmpty() {
            Set<String> set = Opt.<String>empty().toSet();
            Assertions.assertTrue(set.isEmpty());
        }

        @Test
        void testToListSome() {
            List<String> list = Opt.of("test").toList();
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("test", list.getFirst());
        }

        @Test
        void testToListEmpty() {
            List<String> list = Opt.<String>empty().toList();
            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        void testStreamSome() {
            List<String> list = Opt.of("test").stream().toList();
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("test", list.getFirst());
        }

        @Test
        void testStreamEmpty() {
            List<String> list = Opt.<String>empty().stream().toList();
            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        void testUniqueSome() {
            Set<String> set = Opt.of("test").unique();
            Assertions.assertEquals(1, set.size());
            Assertions.assertTrue(set.contains("test"));
        }

        @Test
        void testUniqueEmpty() {
            Set<String> set = Opt.<String>empty().unique();
            Assertions.assertTrue(set.isEmpty());
        }

        @Test
        void testToArraySome() {
            Object[] array = Opt.of("test").toArray();
            Assertions.assertEquals(1, array.length);
            Assertions.assertEquals("test", array[0]);
        }

        @Test
        void testToArrayEmpty() {
            Object[] array = Opt.<String>empty().toArray();
            Assertions.assertEquals(0, array.length);
        }
    }

    @Nested
    class CastTests {
        @Test
        void testCast() {
            Opt<Object> opt = Opt.of("test");
            Opt<String> casted = opt.cast();
            Assertions.assertEquals("test", casted.get());
        }

        @Test
        void testSafeCast() {
            Opt<Object> opt = Opt.of("test");
            Opt<String> casted = Opt.safeCast(opt);
            Assertions.assertEquals("test", casted.get());
        }
    }

    @Nested
    class ConsumerTests {
        @Test
        void testIfPresentSome() {
            List<String> list = new ArrayList<>();
            Opt.of("test").ifPresent(list::add);
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("test", list.getFirst());
        }

        @Test
        void testIfPresentEmpty() {
            List<String> list = new ArrayList<>();
            Opt.<String>empty().ifPresent(list::add);
            Assertions.assertTrue(list.isEmpty());
        }

        @Test
        void testIfPresentOrElseSome() {
            List<String> list = new ArrayList<>();
            Opt.of("test").ifPresentOrElse(list::add, () -> list.add("empty"));
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("test", list.getFirst());
        }

        @Test
        void testIfPresentOrElseEmpty() {
            List<String> list = new ArrayList<>();
            Opt.<String>empty().ifPresentOrElse(list::add, () -> list.add("empty"));
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("empty", list.getFirst());
        }
    }

    @Nested
    class EqualsHashCodeTests {
        @Test
        @SuppressWarnings("EqualsWithItself")
        void testSomeEqualsItself() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertEquals(opt, opt);
        }

        @Test
        void testSomeEqualsSameValue() {
            Opt<String> opt1 = Opt.of("test");
            Opt<String> opt2 = Opt.of("test");
            Assertions.assertEquals(opt1, opt2);
        }

        @Test
        void testSomeNotEqualsDifferentValue() {
            Opt<String> opt1 = Opt.of("test");
            Opt<String> opt2 = Opt.of("other");
            Assertions.assertNotEquals(opt1, opt2);
        }

        @Test
        void testSomeNotEqualsEmpty() {
            Opt<String> opt1 = Opt.of("test");
            Opt<String> opt2 = Opt.empty();
            Assertions.assertNotEquals(opt1, opt2);
        }

        @Test
        @SuppressWarnings("AssertBetweenInconvertibleTypes")
        void testSomeNotEqualsOther() {
            Opt<String> opt = Opt.of("test");
            Assertions.assertNotEquals("test", opt);
        }

        @Test
        void testSomeHashCode() {
            Opt<String> opt1 = Opt.of("test");
            Opt<String> opt2 = Opt.of("test");
            Assertions.assertEquals(opt1.hashCode(), opt2.hashCode());
        }

        @Test
        void testEmptyHashCode() {
            Assertions.assertEquals(Opt.empty().hashCode(), Opt.empty().hashCode());
        }
    }

    @Nested
    class ToStringTests {
        @Test
        void testSomeToString() {
            Assertions.assertEquals("Some(test)", Opt.of("test").toString());
        }

        @Test
        void testEmptyToString() {
            Assertions.assertEquals("Empty", Opt.empty().toString());
        }
    }

    @Nested
    class EdgeCaseTests {
        @Test
        void testNullHandling() {
            Opt<String> opt = Opt.ofNullable(null);
            Assertions.assertFalse(opt.isPresent());
            Assertions.assertNull(opt.orElseNull());
            Assertions.assertEquals("default", opt.orElse("default"));
        }

        @Test
        void testChaining() {
            Opt<Integer> result = Opt.of("  test  ")
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::length);

            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(4, result.get());
        }

        @Test
        void testEmptyChaining() {
            Opt<Integer> result = Opt.<String>empty()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::length);

            Assertions.assertFalse(result.isPresent());
        }

        @Test
        void testMultipleFilters() {
            Opt<String> result = Opt.of("test")
                    .filter(s -> s.length() > 3)
                    .filter(s -> s.startsWith("t"));

            Assertions.assertTrue(result.isPresent());
        }

        @Test
        void testFilterRemoval() {
            Opt<String> result = Opt.of("test")
                    .filter(s -> s.length() > 3)
                    .filter(s -> s.startsWith("x"));

            Assertions.assertFalse(result.isPresent());
        }
    }
}