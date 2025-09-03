package ru.prohor.universe.yahtzee.services.images;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class AvatarGenerator {
    private static final int IMAGE_SIZE = 40;

    private static final List<Color> BACKGROUND = List.of(
            new Color(255, 0, 0),
            new Color(255, 233, 2),
            new Color(72, 255, 0),
            new Color(0, 244, 235),
            new Color(93, 96, 237),
            new Color(198, 106, 228)
    );

    private final Random random = new Random();

    // TODO
    public BufferedImage generate() {
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((java.awt.image.DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixels, BACKGROUND.get(random.nextInt(BACKGROUND.size())).getRGB());
        return image;
    }
}
