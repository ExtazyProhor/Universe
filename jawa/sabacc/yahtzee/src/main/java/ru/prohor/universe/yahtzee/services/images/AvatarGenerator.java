package ru.prohor.universe.yahtzee.services.images;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class AvatarGenerator {
    private static final Color[] BACKGROUND_COLORS = {
            new Color(241, 239, 208),
            new Color(200, 200, 248),
            new Color(251, 224, 224),
            new Color(253, 245, 215),
            new Color(213, 240, 251),
            new Color(198, 253, 229),
            new Color(253, 220, 196),
            new Color(206, 251, 191),
            new Color(253, 189, 183),
            new Color(199, 250, 250)
    };

    private static final Color[] DICE_COLORS = {
            new Color(100, 149, 237),
            new Color(205, 92, 92),
            new Color(60, 179, 113),
            new Color(255, 140, 0),
            new Color(147, 112, 219),
            new Color(73, 158, 230),
            new Color(255, 99, 71),
            new Color(72, 209, 204),
            new Color(218, 165, 32),
            new Color(186, 85, 211)
    };

    private static final Color[] DOTS_COLORS = {
            new Color(13, 13, 74),
            new Color(103, 0, 0),
            new Color(0, 64, 0),
            new Color(97, 74, 0),
            new Color(47, 0, 78),
            new Color(51, 51, 51),
            new Color(0, 0, 0),
            new Color(0, 83, 83),
            new Color(53, 66, 29),
            new Color(45, 36, 87)
    };

    private static final int[][][] DICE_PATTERNS = {
            {{1, 1}}, // 1
            {{0, 0}, {2, 2}}, // 2
            {{0, 0}, {1, 1}, {2, 2}}, // 3
            {{0, 0}, {0, 2}, {2, 0}, {2, 2}}, // 4
            {{0, 0}, {0, 2}, {1, 1}, {2, 0}, {2, 2}}, // 5
            {{0, 0}, {0, 1}, {0, 2}, {2, 0}, {2, 1}, {2, 2}} // 6
    };

    private static final int IMAGE_SIZE = 32;
    private static final int BLACK_RGB = 0;

    private static final int DICE_COLOR_SIZE = 18;
    private static final int BORDER_SIZE = DICE_COLOR_SIZE - 2;
    private static final int DICE_COLOR_INDEX = (IMAGE_SIZE - DICE_COLOR_SIZE) / 2;
    private static final int BLACK_DOT_INDEX = DICE_COLOR_INDEX + DICE_COLOR_SIZE - 1;

    private static final int FIRST_DOT_INDEX = DICE_COLOR_INDEX + 3;
    private static final int SIZE_BETWEEN_DOTS = 5;
    private static final int DOT_SIZE = 2;

    private final Random random = new Random();

    public BufferedImage generate() {
        return generate(
                randomColor(BACKGROUND_COLORS),
                randomColor(DICE_COLORS),
                randomColor(DOTS_COLORS),
                random.nextInt(6)
        );
    }

    private BufferedImage generate(Color bg, Color dice, Color dots, int value) {
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        fillRect(image, bg, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
        fillRect(image, dice, DICE_COLOR_INDEX, DICE_COLOR_INDEX, DICE_COLOR_SIZE, DICE_COLOR_SIZE);

        image.setRGB(DICE_COLOR_INDEX, DICE_COLOR_INDEX, BLACK_RGB);
        image.setRGB(DICE_COLOR_INDEX, BLACK_DOT_INDEX, BLACK_RGB);
        image.setRGB(BLACK_DOT_INDEX, DICE_COLOR_INDEX, BLACK_RGB);
        image.setRGB(BLACK_DOT_INDEX, BLACK_DOT_INDEX, BLACK_RGB);

        fillRect(image, Color.BLACK, DICE_COLOR_INDEX + 1, DICE_COLOR_INDEX - 1, BORDER_SIZE, 1);
        fillRect(image, Color.BLACK, DICE_COLOR_INDEX - 1, DICE_COLOR_INDEX + 1, 1, BORDER_SIZE);
        fillRect(image, Color.BLACK, DICE_COLOR_INDEX + 1, BLACK_DOT_INDEX + 1, BORDER_SIZE, 1);
        fillRect(image, Color.BLACK, BLACK_DOT_INDEX + 1, DICE_COLOR_INDEX + 1, 1, BORDER_SIZE);

        for (int[] dotsPosition : DICE_PATTERNS[value]) {
            fillRect(
                    image,
                    dots,
                    FIRST_DOT_INDEX + dotsPosition[0] * SIZE_BETWEEN_DOTS,
                    FIRST_DOT_INDEX + dotsPosition[1] * SIZE_BETWEEN_DOTS,
                    DOT_SIZE,
                    DOT_SIZE
            );
        }
        return image;
    }

    private Color randomColor(Color[] colors) {
        return colors[random.nextInt(colors.length)];
    }

    private void fillRect(BufferedImage image, Color color, int x, int y, int width, int height) {
        int rgb = color.getRGB();
        for (int i = 0; i < width; ++i)
            for (int j = 0; j < height; ++j)
                image.setRGB(x + i, y + j, rgb);
    }
}
