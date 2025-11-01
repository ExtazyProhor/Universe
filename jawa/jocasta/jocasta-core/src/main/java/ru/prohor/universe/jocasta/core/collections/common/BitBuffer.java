package ru.prohor.universe.jocasta.core.collections.common;

public class BitBuffer {
    private static final int MAX_BIT_COUNT = 64;
    private static final int BITS_IN_BYTE = 8;
    private static final int BITS_IN_INT = 32;
    private static final int BITS_IN_LONG = 64;

    private long buffer;
    private int bitCount;

    public BitBuffer() {
        this.buffer = 0;
        this.bitCount = 0;
    }

    public void addBit(boolean bit) {
        addBit(bit ? 1 : 0);
    }

    public void addBit(long bit) {
        if (bitCount == MAX_BIT_COUNT)
            throw new IllegalArgumentException("buffer overflow");
        buffer = (buffer << 1) | (bit & 1L);
        bitCount++;
    }

    public void addBits(long bits, int bitsCount) {
        if (bitsCount < 0)
            throw new IllegalArgumentException("bitsCount cannot be negative");
        if (bitsCount == 0)
            return;
        if (bitCount + bitsCount > MAX_BIT_COUNT)
            throw new IllegalArgumentException("buffer overflow");
        bitCount += bitsCount;
        buffer = (buffer << bitsCount) | (bitsCount == 64 ? bits : bits & ((1L << bitsCount) - 1));
    }

    public int size() {
        return bitCount;
    }

    public byte extractByte() {
        if (bitCount < BITS_IN_BYTE)
            throw new IllegalStateException("Not enough bits to extract a byte");
        byte result = (byte) ((buffer >> (bitCount - BITS_IN_BYTE)) & 0xFFL);
        buffer &= (1L << (bitCount - BITS_IN_BYTE)) - 1L;
        bitCount -= BITS_IN_BYTE;
        return result;
    }

    public int extractInt() {
        if (bitCount < BITS_IN_INT)
            throw new IllegalStateException("Not enough bits to extract an integer");
        int result = (int) ((buffer >> (bitCount - BITS_IN_INT)) & 0xFFFFFFFFL);
        buffer &= (1L << (bitCount - BITS_IN_INT)) - 1L;
        bitCount -= BITS_IN_INT;
        return result;
    }

    public long extractLong() {
        if (bitCount < BITS_IN_LONG)
            throw new IllegalStateException("Not enough bits to extract a long");
        long result = buffer;
        clear();
        return result;
    }

    public void clear() {
        this.buffer = 0;
        this.bitCount = 0;
    }
}
