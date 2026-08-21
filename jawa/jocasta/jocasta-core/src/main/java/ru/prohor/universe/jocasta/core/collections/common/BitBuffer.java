package ru.prohor.universe.jocasta.core.collections.common;

public class BitBuffer {
    private static final int MAX_BIT_COUNT = 64;
    private static final int BITS_IN_BYTE = 8;
    private static final int BITS_IN_INT = 32;
    private static final int BITS_IN_LONG = 64;

    private long buffer;
    private int bitCount;

    /**
     * Создаёт пустой буфер битов.
     */
    public BitBuffer() {
        this.buffer = 0;
        this.bitCount = 0;
    }

    /**
     * Добавляет один бит в конец буфера.
     *
     * @param bit значение добавляемого бита
     * @throws IllegalArgumentException если буфер уже содержит 64 бита
     */
    public void addBit(boolean bit) {
        addBit(bit ? 1 : 0);
    }

    /**
     * Добавляет младший бит переданного значения в конец буфера.
     *
     * @param bit значение, младший бит которого будет добавлен в буфер
     * @throws IllegalArgumentException если буфер уже содержит 64 бита
     */
    public void addBit(long bit) {
        if (bitCount == MAX_BIT_COUNT)
            throw new IllegalArgumentException("buffer overflow");
        buffer = (buffer << 1) | (bit & 1L);
        bitCount++;
    }

    /**
     * Добавляет указанное количество младших бит переданного значения в конец буфера.
     * Биты добавляются от старшего к младшему.
     *
     * @param bits значение, из которого извлекаются добавляемые биты
     * @param bitsCount количество добавляемых битов, от 0 до 64
     * @throws IllegalArgumentException если {@code bitsCount} отрицательно
     * @throws IllegalArgumentException если после добавления количество битов
     *                                  в буфере превысит 64
     */
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

    /**
     * Возвращает количество битов, хранящихся в буфере.
     *
     * @return количество битов в буфере
     */
    public int size() {
        return bitCount;
    }

    /**
     * Извлекает первые слева 8 бит из буфера и удаляет их из него.
     *
     * @return извлечённые 8 бит в виде {@code byte}
     * @throws IllegalStateException если в буфере содержится менее 8 бит
     */
    public byte extractByte() {
        if (bitCount < BITS_IN_BYTE)
            throw new IllegalStateException("Not enough bits to extract a byte");
        byte result = (byte) ((buffer >> (bitCount - BITS_IN_BYTE)) & 0xFFL);
        buffer &= (1L << (bitCount - BITS_IN_BYTE)) - 1L;
        bitCount -= BITS_IN_BYTE;
        return result;
    }

    /**
     * Извлекает первые слева 32 бита из буфера и удаляет их из него.
     *
     * @return извлечённые 32 бита в виде {@code int}
     * @throws IllegalStateException если в буфере содержится менее 32 бит
     */
    public int extractInt() {
        if (bitCount < BITS_IN_INT)
            throw new IllegalStateException("Not enough bits to extract an integer");
        int result = (int) ((buffer >> (bitCount - BITS_IN_INT)) & 0xFFFFFFFFL);
        buffer &= (1L << (bitCount - BITS_IN_INT)) - 1L;
        bitCount -= BITS_IN_INT;
        return result;
    }

    /**
     * Извлекает все 64 бита из буфера и очищает его.
     *
     * @return содержимое буфера в виде {@code long}
     * @throws IllegalStateException если в буфере содержится менее 64 бит
     */
    public long extractLong() {
        if (bitCount < BITS_IN_LONG)
            throw new IllegalStateException("Not enough bits to extract a long");
        long result = buffer;
        clear();
        return result;
    }

    /**
     * Очищает буфер, удаляя все хранящиеся в нём биты.
     */
    public void clear() {
        this.buffer = 0;
        this.bitCount = 0;
    }
}
