package ru.prohor.universe.jocasta.core.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrdinalNumbersTest {
    @Test
    void testBasicOrdinals() {
        Assertions.assertEquals("1st", OrdinalNumbers.of(1));
        Assertions.assertEquals("2nd", OrdinalNumbers.of(2));
        Assertions.assertEquals("3rd", OrdinalNumbers.of(3));
        Assertions.assertEquals("4th", OrdinalNumbers.of(4));
        Assertions.assertEquals("5th", OrdinalNumbers.of(5));
        Assertions.assertEquals("10th", OrdinalNumbers.of(10));
    }

    @Test
    void testTeensSpecialCases() {
        Assertions.assertEquals("11th", OrdinalNumbers.of(11));
        Assertions.assertEquals("12th", OrdinalNumbers.of(12));
        Assertions.assertEquals("13th", OrdinalNumbers.of(13));
    }

    @Test
    void testEdgeCasesBeyondTeens() {
        Assertions.assertEquals("21st", OrdinalNumbers.of(21));
        Assertions.assertEquals("22nd", OrdinalNumbers.of(22));
        Assertions.assertEquals("23rd", OrdinalNumbers.of(23));
        Assertions.assertEquals("24th", OrdinalNumbers.of(24));

        Assertions.assertEquals("101st", OrdinalNumbers.of(101));
        Assertions.assertEquals("111th", OrdinalNumbers.of(111));
        Assertions.assertEquals("113th", OrdinalNumbers.of(113));
        Assertions.assertEquals("121st", OrdinalNumbers.of(121));
    }

    @Test
    void testLargeNumbers() {
        Assertions.assertEquals("1000th", OrdinalNumbers.of(1000));
        Assertions.assertEquals("1001st", OrdinalNumbers.of(1001));
        Assertions.assertEquals("1002nd", OrdinalNumbers.of(1002));
    }

    @Test
    void testInvalidNumbers() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> OrdinalNumbers.of(0));

        Exception e2 = Assertions.assertThrows(IllegalArgumentException.class, () -> OrdinalNumbers.of(-5));
        Assertions.assertTrue(e2.getMessage().contains("-5"));
    }
}
