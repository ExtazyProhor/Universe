package ru.prohor.universe.padawan.scripts.qr;

public class BitMatrix {
    private final int size;
    private final BitList[] matrix;

    public BitMatrix(int size) {
        if (size < 1)
            throw new IllegalArgumentException("\"size\" must be greater than 0");
        this.size = size;
        this.matrix = new BitList[size];
        for (int i = 0; i < size; ++i)
            matrix[i] = new BitList(size);
    }

    public void setBit(int x, int y, boolean bit) {
        checkRange(x, y);
        matrix[y].set(x, bit);
    }

    public boolean getBit(int x, int y) {
        checkRange(x, y);
        return matrix[y].get(x);
    }

    private void checkRange(int x, int y) {
        if (x < 0 || x >= size)
            throw new IllegalArgumentException("\"x\" must be from 0 to " + size);
        if (y < 0 || y >= size)
            throw new IllegalArgumentException("\"y\" must be from 0 to " + size);
    }

    public int size() {
        return size;
    }
}
