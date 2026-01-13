package ru.prohor.universe.jocasta.core.collections.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartesianProductTest {
    @Test
    @DisplayName("Should create cartesian product with single 'to' parameter")
    void testOfTwoRanges_SingleParameter() {
        var product = CartesianProduct.ofTwoRanges(3);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(9, result.size());
        assertTrue(result.contains(new Tuple2<>(0, 0)));
        assertTrue(result.contains(new Tuple2<>(2, 2)));
    }

    @Test
    @DisplayName("Should create cartesian product with two 'to' parameters")
    void testOfTwoRanges_TwoParameters() {
        var product = CartesianProduct.ofTwoRanges(2, 3);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(6, result.size());
        assertEquals(new Tuple2<>(0, 0), result.get(0));
        assertEquals(new Tuple2<>(0, 1), result.get(1));
        assertEquals(new Tuple2<>(0, 2), result.get(2));
        assertEquals(new Tuple2<>(1, 0), result.get(3));
        assertEquals(new Tuple2<>(1, 1), result.get(4));
        assertEquals(new Tuple2<>(1, 2), result.get(5));
    }

    @Test
    @DisplayName("Should create cartesian product with custom ranges")
    void testOfTwoRanges_CustomRanges() {
        var product = CartesianProduct.ofTwoRanges(1, 3, 2, 5);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(6, result.size());
        assertEquals(new Tuple2<>(1, 2), result.get(0));
        assertEquals(new Tuple2<>(1, 3), result.get(1));
        assertEquals(new Tuple2<>(1, 4), result.get(2));
        assertEquals(new Tuple2<>(2, 2), result.get(3));
        assertEquals(new Tuple2<>(2, 3), result.get(4));
        assertEquals(new Tuple2<>(2, 4), result.get(5));
    }

    @Test
    @DisplayName("Should handle empty ranges (from == to)")
    void testOfTwoRanges_EmptyRanges() {
        var product = CartesianProduct.ofTwoRanges(0, 0, 0, 0);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should handle one empty range")
    void testOfTwoRanges_OneEmptyRange() {
        var product = CartesianProduct.ofTwoRanges(0, 3, 5, 5);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw exception when from > to in first range")
    void testOfTwoRanges_InvalidFirstRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CartesianProduct.ofTwoRanges(5, 3, 0, 2)
        );
    }

    @Test
    @DisplayName("Should throw exception when from > to in second range")
    void testOfTwoRanges_InvalidSecondRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CartesianProduct.ofTwoRanges(0, 3, 5, 2)
        );
    }

    @Test
    @DisplayName("Should apply mapper function correctly")
    void testMap_WithMapper() {
        var product = CartesianProduct.ofTwoRanges(2, 3);
        List<Integer> result = product.map((i, j) -> i * 10 + j);

        assertEquals(6, result.size());
        assertEquals(List.of(0, 1, 2, 10, 11, 12), result);
    }

    @Test
    @DisplayName("Should map to strings")
    void testMap_ToStrings() {
        var product = CartesianProduct.ofTwoRanges(1, 3, 1, 3);
        List<String> result = product.map((i, j) -> i + "," + j);

        assertEquals(4, result.size());
        assertTrue(result.contains("1,1"));
        assertTrue(result.contains("2,2"));
    }

    @Test
    @DisplayName("Should map to complex objects")
    void testMap_ToComplexObjects() {
        record Point(int x, int y) {}

        var product = CartesianProduct.ofTwoRanges(0, 2, 0, 2);
        List<Point> result = product.map(Point::new);

        assertEquals(4, result.size());
        assertTrue(result.contains(new Point(0, 0)));
        assertTrue(result.contains(new Point(1, 1)));
    }

    @Test
    @DisplayName("Should handle negative ranges")
    void testOfTwoRanges_NegativeRanges() {
        var product = CartesianProduct.ofTwoRanges(-2, 0, -1, 1);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(4, result.size());
        assertTrue(result.contains(new Tuple2<>(-2, -1)));
        assertTrue(result.contains(new Tuple2<>(-2, 0)));
        assertTrue(result.contains(new Tuple2<>(-1, -1)));
        assertTrue(result.contains(new Tuple2<>(-1, 0)));
    }

    @Test
    @DisplayName("Should maintain correct order in cartesian product")
    void testAsTuplesList_OrderIsCorrect() {
        var product = CartesianProduct.ofTwoRanges(0, 2, 0, 3);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        List<Tuple2<Integer, Integer>> expected = List.of(
                new Tuple2<>(0, 0), new Tuple2<>(0, 1), new Tuple2<>(0, 2),
                new Tuple2<>(1, 0), new Tuple2<>(1, 1), new Tuple2<>(1, 2)
        );

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1, 0, 1, 1",
            "0, 2, 0, 2, 4",
            "0, 3, 0, 4, 12",
            "1, 4, 2, 5, 9"
    })
    @DisplayName("Should produce correct size for various ranges")
    void testSize_VariousRanges(int from1, int to1, int from2, int to2, int expectedSize) {
        var product = CartesianProduct.ofTwoRanges(from1, to1, from2, to2);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(expectedSize, result.size());
    }

    @Test
    @DisplayName("Should handle large ranges efficiently")
    void testOfTwoRanges_LargeRanges() {
        var product = CartesianProduct.ofTwoRanges(0, 100, 0, 100);
        List<Tuple2<Integer, Integer>> result = product.asTuplesList();

        assertEquals(10000, result.size());
        assertEquals(new Tuple2<>(0, 0), result.getFirst());
        assertEquals(new Tuple2<>(99, 99), result.get(9999));
    }

    @Test
    @DisplayName("Should allow multiple map calls on same product")
    void testMap_MultipleCallsOnSameProduct() {
        var product = CartesianProduct.ofTwoRanges(0, 2, 0, 2);

        List<Integer> sums = product.map(Integer::sum);
        List<Integer> products = product.map((i, j) -> i * j);

        assertEquals(4, sums.size());
        assertEquals(4, products.size());
        assertEquals(List.of(0, 1, 1, 2), sums);
        assertEquals(List.of(0, 0, 0, 1), products);
    }
}
