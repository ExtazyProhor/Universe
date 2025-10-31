package ru.prohor.universe.padawan.scripts.qr;

public enum QRCorrectionLevel {
    L(0),
    M(1),
    Q(2),
    H(3);

    final int index;

    QRCorrectionLevel(int index) {
        this.index = index;
    }
}
