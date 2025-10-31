package ru.prohor.universe.padawan.scripts.qr;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TwoColorImage extends BitMatrix {
    private int scale = 1;
    private int offset = 0;
    private int targetSize;

    public TwoColorImage(int size) {
        super(size);
        targetSize = size;
    }

    public TwoColorImage setOffset(int offset) {
        if (offset < 0) throw new IllegalArgumentException("\"offset\" must be not negative");
        this.offset = offset;
        return this;
    }

    public TwoColorImage setScale(int scale) {
        if (scale <= 0) throw new IllegalArgumentException("\"scale\" must be positive");
        this.scale = scale;
        return this;
    }

    public TwoColorImage setTargetSize(int targetSize) {
        if (targetSize < size())
            throw new IllegalArgumentException("\"target size\" must be equal or greater than size");
        this.targetSize = targetSize;
        return this;
    }

    public BufferedImage toImage(Color mainColor, Color secondColor) {
        return toImage(mainColor.getRGB(), secondColor.getRGB(), BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage toImage() {
        return toImage(Color.WHITE.getRGB(), Color.BLACK.getRGB(), BufferedImage.TYPE_BYTE_BINARY);
    }

    private BufferedImage toImage(int color1, int color2, int imageType) {
        if (targetSize < size() + offset)
            throw new RuntimeException("\"target size\" must be equal or greater than size + offset");
        BufferedImage image = new BufferedImage(targetSize * scale, targetSize * scale, imageType);
        for (int i = 0; i < targetSize; ++i) {
            for (int j = 0; j < targetSize; ++j) {
                int color = i >= offset && j >= offset && i < offset + this.size() && j < offset + this.size()
                        && getBit(i - offset, j - offset) ? color2 : color1;
                for (int k = 0; k < scale; ++k)
                    for (int l = 0; l < scale; ++l)
                        image.setRGB(i * scale + k, j * scale + l, color);
            }
        }
        return image;
    }
}
