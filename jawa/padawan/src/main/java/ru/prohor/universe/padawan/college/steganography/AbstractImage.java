package ru.prohor.universe.padawan.college.steganography;

import java.awt.image.BufferedImage;

public abstract class AbstractImage {
    protected static final RuntimeException ILLEGAL_PIXELS_USE = new IllegalArgumentException(
            "from 1 to 8 pixels can be changed in one byte"
    );
    protected static final int DEFAULT_PIXELS_USE = 3;

    protected final BufferedImage image;

    protected AbstractImage(BufferedImage image) {
        this.image = image;
    }

    public int height() {
        return image.getHeight();
    }

    public int width() {
        return image.getWidth();
    }

    protected int getAvailableBytes(int pixelsUse) {
        return width() * height() * 3 * pixelsUse / 8;
    }
}
