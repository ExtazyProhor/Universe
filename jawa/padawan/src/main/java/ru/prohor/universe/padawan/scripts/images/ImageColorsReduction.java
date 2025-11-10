package ru.prohor.universe.padawan.scripts.images;

import ru.prohor.universe.jocasta.core.utils.FileSystemUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ImageColorsReduction {
    private static final int PIXELATION = 1;
    private static final double SQUARE_PIX = PIXELATION * PIXELATION;
    private static final int COLOR_LEVELS = 5;
    private static final double LEVEL_COUNT = 256.0 / (COLOR_LEVELS - 1);

    public static void main(String[] args) throws Exception {
        compressImage(FileSystemUtils.userHome().asString() + "/image.png", "image2.png");
        compressImageBW(FileSystemUtils.userHome().asString() + "/image.png", "image1.png");
    }

    private static void compressImageBW(String input, String output) throws Exception {
        BufferedImage source = ImageIO.read(new File(input));
        BufferedImage image = new BufferedImage(
                source.getWidth() / PIXELATION * PIXELATION,
                source.getHeight() / PIXELATION * PIXELATION,
                BufferedImage.TYPE_INT_RGB
        );
        for (int i = 0; i * PIXELATION < image.getWidth(); ++i) {
            for (int j = 0; j * PIXELATION < image.getHeight(); ++j) {
                int r = 0, g = 0, b = 0;
                for (int k = 0; k < PIXELATION; ++k) {
                    for (int l = 0; l < PIXELATION; ++l) {
                        Color color = new Color(source.getRGB(i * PIXELATION + k, j * PIXELATION + l));
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                    }
                }

                int color = compressColorBW(r, g, b);
                for (int k = 0; k < PIXELATION; ++k) {
                    for (int l = 0; l < PIXELATION; ++l) {
                        image.setRGB(i * PIXELATION + k, j * PIXELATION + l, color);
                    }
                }
            }
        }
        ImageIO.write(image, "png", new File(output));
    }

    private static void compressImage(String input, String output) throws Exception {
        BufferedImage source = ImageIO.read(new File(input));
        BufferedImage image = new BufferedImage(
                source.getWidth() / PIXELATION * PIXELATION,
                source.getHeight() / PIXELATION * PIXELATION,
                BufferedImage.TYPE_INT_RGB
        );
        for (int i = 0; i * PIXELATION < image.getWidth(); ++i) {
            for (int j = 0; j * PIXELATION < image.getHeight(); ++j) {
                int r = 0, g = 0, b = 0;
                for (int k = 0; k < PIXELATION; ++k) {
                    for (int l = 0; l < PIXELATION; ++l) {
                        Color color = new Color(source.getRGB(i * PIXELATION + k, j * PIXELATION + l));
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                    }
                }
                int color = new Color(compressColor(r), compressColor(g), compressColor(b)).getRGB();
                for (int k = 0; k < PIXELATION; ++k) {
                    for (int l = 0; l < PIXELATION; ++l) {
                        image.setRGB(i * PIXELATION + k, j * PIXELATION + l, color);
                    }
                }
            }
        }
        ImageIO.write(image, "png", new File(output));
    }

    private static int colorsCount(String path) throws Exception {
        BufferedImage image = ImageIO.read(new File(path));
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                set.add(image.getRGB(i, j));
            }
        }
        return set.size();
    }

    private static int compressColorBW(double r, double g, double b) {
        double shade = (0.21 * r + 0.71 * g + 0.07 * b) / SQUARE_PIX;
        int color = (int) Math.round(Math.round(shade / LEVEL_COUNT) * LEVEL_COUNT);
        if (color < 0)
            color = 0;
        else if (color > 255)
            color = 255;
        return new Color(color, color, color).getRGB();
    }

    private static int compressColor(int color) {
        color = (int) Math.round(Math.round(color / SQUARE_PIX / LEVEL_COUNT) * LEVEL_COUNT);
        if (color < 0)
            return 0;
        return Math.min(255, color);
    }
}
