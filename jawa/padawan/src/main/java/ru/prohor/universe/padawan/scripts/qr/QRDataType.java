package ru.prohor.universe.padawan.scripts.qr;

public enum QRDataType {
    DIGITS(0),
    CHARACTERS(1),
    BYTES(2);

    final int index;

    QRDataType(int index) {
        this.index = index;
    }
}
