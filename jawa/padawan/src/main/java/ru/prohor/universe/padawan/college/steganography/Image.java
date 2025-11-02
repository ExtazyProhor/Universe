package ru.prohor.universe.padawan.college.steganography;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class Image extends AbstractImage {
    public Image(BufferedImage image) {
        super(image);
    }

    public void encrypt(String message) {
        encrypt(message.getBytes());
    }

    public void encrypt(String message, int pixelsUse) {
        encrypt(message.getBytes(), pixelsUse);
    }

    public void encrypt(byte[] data) {
        encrypt(data, DEFAULT_PIXELS_USE);
    }

    public void encrypt(byte[] data, int pixelsUse) {
        if (pixelsUse < 1 || pixelsUse > 8)
            throw ILLEGAL_PIXELS_USE;
        if (getAvailableBytes(pixelsUse) < data.length + 4)
            throw new IllegalArgumentException(
                    "too much data: " + data.length + " bytes, image resolution only " + width() + "x" + height()
            );

        byte[] dataLength = ByteBuffer.allocate(4).putInt(data.length).array();
        byte[] fullData = new byte[data.length + dataLength.length];
        System.arraycopy(dataLength, 0, fullData, 0, dataLength.length);
        System.arraycopy(data, 0, fullData, dataLength.length, data.length);

        pixelsUse--;
        Pointers p = new Pointers();

        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                Color color = new Color(image.getRGB(x, y));
                int red = changeColor(p, fullData, color.getRed(), pixelsUse);
                int green = changeColor(p, fullData, color.getGreen(), pixelsUse);
                int blue = changeColor(p, fullData, color.getBlue(), pixelsUse);
                image.setRGB(x, y, new Color(red, green, blue, color.getAlpha()).getRGB());
                if (p.end)
                    return;
            }
        }
    }

    private int changeColor(Pointers p, byte[] data, int currentColor, int pixelsUse) {
        if (p.end)
            return currentColor;
        for (int i = pixelsUse; i >= 0; --i) {
            currentColor = extractBitAndShift(currentColor, data[p.arrPointer], p.arrBitPointer, i);
            p.arrBitPointer++;
            if (p.arrBitPointer == 8) {
                p.arrBitPointer = 0;
                p.arrPointer++;
                if (p.arrPointer == data.length) {
                    p.end = true;
                    break;
                }
            }
        }
        return currentColor;
    }

    private int extractBitAndShift(int newColor, byte data, int bit, int position) {
        return (data >>> (7 - bit) & 1) == 0 ? newColor & ~(1 << position) : newColor | (1 << position);
    }

    private static class Pointers {
        private int arrPointer = 0;
        private int arrBitPointer = 0;
        private boolean end = false;
    }
}
