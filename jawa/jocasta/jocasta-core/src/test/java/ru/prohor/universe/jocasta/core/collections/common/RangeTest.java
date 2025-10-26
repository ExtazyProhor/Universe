package ru.prohor.universe.jocasta.core.collections.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class RangeTest {
    // @Test
    void testRangeWithSingleParameter() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(5)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(0, 1, 2, 3, 4), result);
    }

    // @Test
    void testRangeWithSingleParameterZero() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithSingleParameterNegative() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(-3)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithTwoParameters() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(2, 7)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(2, 3, 4, 5, 6), result);
    }

    // @Test
    void testRangeWithTwoParametersEqual() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(5, 5)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithTwoParametersReversed() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(5, 2)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithPositiveStep() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 10, 2)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(0, 2, 4, 6, 8), result);
    }

    // @Test
    void testRangeWithPositiveStepLargerThanRange() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 5, 10)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(0), result);
    }

    // @Test
    void testRangeWithNegativeStep() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(10, 0, -2)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(10, 8, 6, 4, 2), result);
    }

    // @Test
    void testRangeWithNegativeStepFromNegativeToNegative() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(-2, -8, -2)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(-2, -4, -6), result);
    }

    // @Test
    void testRangeWithNegativeStepWrongDirection() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 10, -1)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithPositiveStepWrongDirection() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(10, 0, 1)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeWithZeroStepThrowsException() {
        // RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
        //     Range.range(0, 10, 0);
        // });
        // Assertions.assertEquals("Step must not be 0", exception.getMessage());
    }

    // @Test
    void testRangeWithNegativeNumbers() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(-5, -1)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(-5, -4, -3, -2), result);
    }

    // @Test
    void testRangeWithNegativeToPositive() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(-2, 3)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(-2, -1, 0, 1, 2), result);
    }

    // @Test
    void testIteratorHasNextBeforeIteration() {
        Iterator<Integer> iterator = Range.range(0, 3).iterator();
        // Assertions.assertTrue(iterator.hasNext());
    }

    // @Test
    void testIteratorHasNextAfterIteration() {
        Iterator<Integer> iterator = Range.range(0, 2).iterator();
        iterator.next();
        iterator.next();
        // Assertions.assertFalse(iterator.hasNext());
    }

    // @Test
    void testIteratorNextReturnsCorrectValues() {
        Iterator<Integer> iterator = Range.range(5, 8).iterator();
        // Assertions.assertEquals(5, iterator.next());
        // Assertions.assertEquals(6, iterator.next());
        // Assertions.assertEquals(7, iterator.next());
    }

    // @Test
    void testMultipleIteratorsIndependent() {
        Iterable<Integer> range = Range.range(0, 3);
        Iterator<Integer> iterator1 = range.iterator();
        Iterator<Integer> iterator2 = range.iterator();

        // Assertions.assertEquals(0, iterator1.next());
        // Assertions.assertEquals(0, iterator2.next());
        // Assertions.assertEquals(1, iterator1.next());
        // Assertions.assertEquals(1, iterator2.next());
    }

    // @Test
    void testRangeWithLargeStep() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 100, 25)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(0, 25, 50, 75), result);
    }

    // @Test
    void testRangeWithStepOne() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 3, 1)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(0, 1, 2), result);
    }

    // @Test
    void testEmptyRangeWithNegativeStep() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(0, 0, -1)) {
            result.add(value);
        }
        // Assertions.assertEquals(List.of(), result);
    }

    // @Test
    void testRangeFromMaxToMin() {
        List<Integer> result = new ArrayList<>();
        for (Integer value : Range.range(Integer.MAX_VALUE - 3, Integer.MAX_VALUE)) {
            result.add(value);
        }
        // Assertions.assertEquals(
        //     List.of(Integer.MAX_VALUE - 3, Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1), result
        // );
    }
}
