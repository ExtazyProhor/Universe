package ru.prohor.universe.padawan.college.steganography;

import ru.prohor.universe.jocasta.core.collections.common.BitBuffer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageWithCipher extends AbstractImage {
    public ImageWithCipher(BufferedImage image) {
        super(image);
    }

    public String decryptString() {
        return new String(decrypt());
    }

    public String decryptString(int pixelsUse) {
        return new String(decrypt(pixelsUse));
    }

    public byte[] decrypt() {
        return decrypt(DEFAULT_PIXELS_USE);
    }

    public byte[] decrypt(int pixelsUse) {
        if (pixelsUse < 1 || pixelsUse > 8)
            throw ILLEGAL_PIXELS_USE;
        int availableBytes = getAvailableBytes(pixelsUse);
        if (availableBytes < 5)
            throw new IllegalArgumentException("too few bytes available");

        boolean formationTitle = true;
        int bytesCount = 4;
        byte[] bytes = new byte[bytesCount];
        int bytePointer = 0;
        BitBuffer bitBuffer = new BitBuffer();

        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                while (bitBuffer.size() >= 8) {
                    bytes[bytePointer++] = bitBuffer.extractByte();
                    if (bytePointer == bytesCount) {
                        if (!formationTitle)
                            return bytes;
                        bytesCount = ByteBuffer.wrap(bytes).getInt();
                        if (bytesCount + 4 > availableBytes)
                            throw new IllegalArgumentException("header specifies too many bytes");
                        bytes = new byte[bytesCount];
                        formationTitle = false;
                        bytePointer = 0;
                    }
                }
                Color color = new Color(image.getRGB(x, y));
                bitBuffer.addBits(color.getRed(), pixelsUse);
                bitBuffer.addBits(color.getGreen(), pixelsUse);
                bitBuffer.addBits(color.getBlue(), pixelsUse);
            }
        }
        throw new RuntimeException();
    }

    public void save(String path) throws IOException {
        save(new File(path));
    }

    public void save(File file) throws IOException {
        ImageIO.write(image, "png", file);
    }
}
