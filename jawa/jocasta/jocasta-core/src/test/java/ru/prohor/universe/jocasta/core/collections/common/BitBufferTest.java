package ru.prohor.universe.jocasta.core.collections.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BitBufferTest {

    private BitBuffer buffer;

    @BeforeEach
    public void setUp() {
        buffer = new BitBuffer();
    }

    @Test
    public void testInitialState() {
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testAddSingleBitBoolean() {
        buffer.addBit(true);
        Assertions.assertEquals(1, buffer.size());

        buffer.addBit(false);
        Assertions.assertEquals(2, buffer.size());
    }

    @Test
    public void testAddSingleBitLong() {
        buffer.addBit(1L);
        Assertions.assertEquals(1, buffer.size());

        buffer.addBit(0L);
        Assertions.assertEquals(2, buffer.size());
    }

    @Test
    public void testAddBitMasking() {
        buffer.addBit(0b1010L);
        buffer.addBit(0b1011L);
        Assertions.assertEquals(2, buffer.size());
    }

    @Test
    public void testAddBits() {
        buffer.addBits(0b1010, 4);
        Assertions.assertEquals(4, buffer.size());
    }

    @Test
    public void testAddBitsZeroCount() {
        buffer.addBits(0xFF, 0);
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testAddBitsNegativeCount() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> buffer.addBits(0xFF, -1));
    }

    @Test
    public void testBufferOverflowSingleBit() {
        for (int i = 0; i < 64; ++i)
            buffer.addBit(true);
        Assertions.assertEquals(64, buffer.size());
        Assertions.assertThrows(IllegalArgumentException.class, () -> buffer.addBit(true));
    }

    @Test
    public void testBufferOverflowAddBits() {
        buffer.addBits(0xFFFFFFFFFFFFFFFFL, 64);
        Assertions.assertThrows(IllegalArgumentException.class, () -> buffer.addBits(1, 1));
    }

    @Test
    public void testBufferOverflowPartial() {
        buffer.addBits(0xFF, 60);
        Assertions.assertThrows(IllegalArgumentException.class, () -> buffer.addBits(0xFF, 5));
    }

    @Test
    public void testExtractByte() {
        buffer.addBits(0b11001010, 8);
        byte result = buffer.extractByte();

        Assertions.assertEquals((byte) 0b11001010, result);
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testExtractByteNotEnoughBits() {
        buffer.addBits(0b1010, 4);
        Assertions.assertThrows(IllegalStateException.class, () -> buffer.extractByte());
    }

    @Test
    public void testExtractByteFromLargerBuffer() {
        buffer.addBits(0b111100001010, 12);
        byte result = buffer.extractByte();

        Assertions.assertEquals((byte) 0b11110000, result);
        Assertions.assertEquals(4, buffer.size());
    }

    @Test
    public void testExtractInt() {
        buffer.addBits(0x12345678, 32);
        int result = buffer.extractInt();

        Assertions.assertEquals(0x12345678, result);
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testExtractIntNotEnoughBits() {
        buffer.addBits(0xFF, 16);
        Assertions.assertThrows(IllegalStateException.class, () -> buffer.extractInt());
    }

    @Test
    public void testExtractIntFromLargerBuffer() {
        buffer.addBits(0xFFFFL, 16);
        buffer.addBits(0x12345678L, 32);

        int result = buffer.extractInt();
        Assertions.assertEquals(0xFFFF1234, result);
        Assertions.assertEquals(16, buffer.size());
    }

    @Test
    public void testExtractLong() {
        long expected = 0x123456789ABCDEFL;
        buffer.addBits(expected, 64);

        long result = buffer.extractLong();
        Assertions.assertEquals(expected, result);
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testExtractLongNotEnoughBits() {
        buffer.addBits(0xFFFFFFFFL, 32);
        Assertions.assertThrows(IllegalStateException.class, () -> buffer.extractLong());
    }

    @Test
    public void testClear() {
        buffer.addBits(0xFFFFL, 16);
        Assertions.assertEquals(16, buffer.size());

        buffer.clear();
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testMultipleOperations() {
        buffer.addBit(true);
        buffer.addBit(false);
        buffer.addBits(0b111100, 6);

        Assertions.assertEquals(8, buffer.size());

        byte result = buffer.extractByte();
        Assertions.assertEquals((byte) 0b10111100, result);
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testSignedByteExtraction() {
        buffer.addBits(0xFF, 8);
        byte result = buffer.extractByte();

        Assertions.assertEquals((byte) 0xFF, result);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void testSignedIntExtraction() {
        buffer.addBits(0xFFFFFFFFL, 32);
        int result = buffer.extractInt();

        Assertions.assertEquals(-1, result);
    }

    @Test
    public void testAddBits64Bits() {
        long value = 0x123456789ABCDEFL;
        buffer.addBits(value, 64);

        Assertions.assertEquals(64, buffer.size());
        long result = buffer.extractLong();
        Assertions.assertEquals(value, result);
    }

    @Test
    public void testBitMaskingInAddBits() {
        buffer.addBits(0xFFFF, 8);
        byte result = buffer.extractByte();
        Assertions.assertEquals((byte) 0xFF, result);
    }

    @Test
    public void testSequentialExtractions() {
        buffer.addBits(0x12, 8);
        buffer.addBits(0x34, 8);
        buffer.addBits(0x56, 8);

        Assertions.assertEquals((byte) 0x12, buffer.extractByte());
        Assertions.assertEquals((byte) 0x34, buffer.extractByte());
        Assertions.assertEquals((byte) 0x56, buffer.extractByte());
        Assertions.assertEquals(0, buffer.size());
    }

    @Test
    public void testReuseAfterClear() {
        buffer.addBits(0xFF, 8);
        buffer.clear();
        buffer.addBits(0xAA, 8);

        Assertions.assertEquals((byte) 0xAA, buffer.extractByte());
    }
}
