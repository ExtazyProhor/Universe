package ru.prohor.universe.padawan.scripts.qr;

import jakarta.annotation.Nonnull;

import java.util.BitSet;

public class BitList extends BitSet {
    private int len = 0;

    public BitList() {
        super();
    }

    public BitList(int bits) {
        super(bits);
    }

    private void shift(int bitsCount, int shift) {
        for (int i = 0; i < bitsCount; ++i) {
            set(shift + len - 1 - i, get(len - 1 - i));
        }
    }

    public void addAll(long values, int bits) {
        if (bits < 1 || bits > 64)
            throw new IllegalArgumentException("\"bits\" must be from 1 to 64");
        len += bits;
        for (int i = 0; i < bits; ++i) {
            set(len - i - 1, (values & 1) == 1);
            values >>>= 1;
        }
    }

    public void addAll(long values, int bits, int startIndex) {
        if (bits < 1 || bits > 64)
            throw new IllegalArgumentException("\"bits\" must be from 1 to 64");
        if (startIndex < 0 || startIndex > len)
            throw new IllegalArgumentException("\"startIndex\" must be from 0 to len");
        shift(len - startIndex, bits);
        len += bits;
        for (int i = 0; i < bits; ++i) {
            set(startIndex + bits - i - 1, (values & 1) == 1);
            values >>>= 1;
        }
    }

    public void add(boolean val) {
        set(len++, val);
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            if (get(i))
                b.append(1);
            else
                b.append(0);
        }
        return b.toString();
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        int newSize = (len + 7) / 8;
        byte[] arr = new byte[newSize];
        for (int i = 0; i < newSize; ++i) {
            byte b = 0;
            for (int j = 0; j < 8; ++j) {
                b <<= 1;
                if (get(i * 8 + j))
                    b |= 1;
            }
            arr[i] = b;
        }
        return arr;
    }
}
