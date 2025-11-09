package ru.prohor.universe.padawan.scripts.randcolor;

import ru.prohor.universe.padawan.scripts.randcolor.api.Luminosity;
import ru.prohor.universe.padawan.scripts.randcolor.api.RandomColorProvider;
import ru.prohor.universe.padawan.scripts.randcolor.api.SaturationType;
import ru.prohor.universe.padawan.scripts.randcolor.api.Shade;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RandomColorProviderTest {
    private static final String OUTPUT_DIR = "padawan/target/colors";

    private static final int TILE_SIZE = 50;
    private static final int TILES_PER_ROW = 10;
    private static final int COLOR_COUNT = 50;

    public static void main(String[] args) throws Exception {
        File output = new File(OUTPUT_DIR);
        if (!output.exists() && !output.mkdirs())
            throw new IOException("Cannot create directory");

        RandomColorProvider provider = RandomColorProvider.getInstance();
        System.out.println("Starting visual testing of RandomColorProvider");

        testBasicColors(provider);
        testAllShades(provider);
        testAllShadesComparison(provider);
        testShadesWithLuminosity(provider);
        testSpecificHues(provider);
        testSaturationTypes(provider);
        testCombinations(provider);

        System.out.println("Success");
    }

    private static void testBasicColors(RandomColorProvider provider) throws Exception {
        System.out.println("Test 1: Basic colors (getColor)");
        int[] colors = provider.getColors(COLOR_COUNT);
        saveColorPalette(colors, "basic_colors.png");
    }

    private static void testAllShades(RandomColorProvider provider) throws Exception {
        System.out.println("Test 2: All shades");
        for (Shade shade : Shade.values()) {
            int[] colors = provider.getColors(shade, COLOR_COUNT);
            String filename = "shade_" + shade.name().toLowerCase() + ".png";
            saveColorPalette(colors, filename);
        }
    }

    private static void testAllShadesComparison(RandomColorProvider provider) throws Exception {
        System.out.println("Test 3: All shades comparison (one row per shade)");
        Shade[] shades = Shade.values();
        int colorsPerShade = TILES_PER_ROW;
        int totalColors = shades.length * colorsPerShade;
        int[] colors = new int[totalColors];

        for (int i = 0; i < shades.length; i++) {
            int[] shadeColors = provider.getColors(shades[i], colorsPerShade);
            System.arraycopy(shadeColors, 0, colors, i * colorsPerShade, colorsPerShade);
        }

        saveColorPalette(colors, "all_shades_comparison.png");
    }

    private static void testShadesWithLuminosity(RandomColorProvider provider) throws Exception {
        System.out.println("Test 4: Shades with different luminosity");
        for (Shade shade : Shade.values()) {
            for (Luminosity luminosity : Luminosity.values()) {
                int[] colors = provider.getColors(shade, luminosity, COLOR_COUNT);
                String filename = "shade_" + shade.name().toLowerCase() +
                        "_luminosity_" + luminosity.name().toLowerCase() + ".png";
                saveColorPalette(colors, filename);
            }
        }
    }

    private static void testSpecificHues(RandomColorProvider provider) throws Exception {
        System.out.println("Test 5: Specific hue values");
        int[] hueValues = {0, 30, 60, 120, 180, 240, 300, 360};

        for (int hue : hueValues) {
            int[] colors = provider.getColors(
                    hue, SaturationType.NATURAL,
                    Luminosity.NORMAL, COLOR_COUNT
            );
            String filename = "hue_" + hue + "_natural_normal.png";
            saveColorPalette(colors, filename);
        }
    }

    private static void testSaturationTypes(RandomColorProvider provider) throws Exception {
        System.out.println("Test 6: Saturation types");
        for (SaturationType satType : SaturationType.values()) {
            int[] colors = provider.getColors(180, satType, Luminosity.NORMAL, COLOR_COUNT);
            String filename = "saturation_" + satType.name().toLowerCase() + ".png";
            saveColorPalette(colors, filename);
        }
    }

    private static void testCombinations(RandomColorProvider provider) throws Exception {
        System.out.println("Test 7: Parameter combinations");

        for (Luminosity lum : new Luminosity[]{Luminosity.BRIGHT, Luminosity.DARK, Luminosity.LIGHT}) {
            int[] colors = provider.getColors(0, SaturationType.NATURAL, lum, COLOR_COUNT);
            String filename = "combo_red_" + lum.name().toLowerCase() + ".png";
            saveColorPalette(colors, filename);
        }

        int[] colors = provider.getColors(
                240, SaturationType.MONOCHROME,
                Luminosity.NORMAL, COLOR_COUNT
        );
        saveColorPalette(colors, "combo_blue_monochrome.png");

        colors = provider.getColors(
                120,
                SaturationType.RANDOM,
                Luminosity.RANDOM,
                COLOR_COUNT
        );
        saveColorPalette(colors, "combo_green_random_all.png");
    }

    private static void saveColorPalette(int[] colors, String filename) throws Exception {
        int rows = (int) Math.ceil((double) colors.length / TILES_PER_ROW);
        int width = TILES_PER_ROW * TILE_SIZE;
        int height = rows * TILE_SIZE;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        for (int i = 0; i < colors.length; i++) {
            int x = (i % TILES_PER_ROW) * TILE_SIZE;
            int y = (i / TILES_PER_ROW) * TILE_SIZE;

            g2d.setColor(new Color(colors[i]));
            g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
        }

        g2d.dispose();

        File outputFile = new File(OUTPUT_DIR, filename);
        ImageIO.write(image, "png", outputFile);
        System.out.println("  Created: " + filename);
    }
}
