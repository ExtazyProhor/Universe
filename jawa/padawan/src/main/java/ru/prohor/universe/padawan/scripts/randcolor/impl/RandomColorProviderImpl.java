package ru.prohor.universe.padawan.scripts.randcolor.impl;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.padawan.scripts.randcolor.api.Luminosity;
import ru.prohor.universe.padawan.scripts.randcolor.api.SaturationType;
import ru.prohor.universe.padawan.scripts.randcolor.api.Shade;
import ru.prohor.universe.padawan.scripts.randcolor.api.RandomColorProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomColorProviderImpl implements RandomColorProvider {
    private static final Map<Shade, ColorInfo> COLORS = Arrays.stream(Shade.values()).collect(Collectors.toMap(
            MonoFunction.identity(),
            Shade::getColorInfo
    ));
    private static final Random RANDOM = new Random();

    private static int getColor(int hue, int saturation, int brightness) {
        float h = (hue % 360) / 60f;
        float s = saturation / 100f;
        float v = brightness / 100f;

        int i = (int) Math.floor(h);
        float f = h - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        float r = 0;
        float g = 0;
        float b = 0;

        switch (i) {
            case 0 -> {
                r = v;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = v;
                b = p;
            }
            case 2 -> {
                r = p;
                g = v;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = v;
            }
            case 4 -> {
                r = t;
                g = p;
                b = v;
            }
            case 5 -> {
                r = v;
                g = p;
                b = q;
            }
        }

        int ir = (int) (r * 255);
        int ig = (int) (g * 255);
        int ib = (int) (b * 255);

        return (255 << 24) | (ir << 16) | (ig << 8) | ib;
    }

    @Override
    public int getColor(int hue, SaturationType saturationType, Luminosity luminosity) {
        hue = pickHue(hue);
        int saturation = pickSaturation(hue, saturationType, luminosity);
        int brightness = pickBrightness(getColorInfo(hue), saturation, luminosity);
        return getColor(hue, saturation, brightness);
    }

    @Override
    public int getColor(Shade shade, Luminosity luminosity) {
        int hue = doPickHue(COLORS.get(shade).hueRange());
        int saturation = pickSaturation(shade, SaturationType.NATURAL, luminosity);
        int brightness = pickBrightness(Opt.of(COLORS.get(shade)), saturation, luminosity);
        return getColor(hue, saturation, brightness);
    }

    private static int pickHue(int hue) {
        return doPickHue(Opt.of(getHueRange(hue)));
    }

    private static int doPickHue(Opt<Range> hueRange) {
        int hue = hueRange.map(RandomColorProviderImpl::randomWithin).orElse(0);
        if (hue < 0)
            hue = 360 + hue;
        return hue;
    }

    private static Range getHueRange(int number) {
        if (number < 360 && number > 0)
            return new Range(number, number);
        return new Range(0, 360);
    }

    private static int pickSaturation(int hue, SaturationType saturationType, Luminosity luminosity) {
        return pickSaturation(getColorInfo(hue), saturationType, luminosity);
    }

    private static int pickSaturation(Shade shade, SaturationType saturationType, Luminosity luminosity) {
        ColorInfo colorInfo = COLORS.get(shade);
        return pickSaturation(Opt.of(colorInfo), saturationType, luminosity);
    }

    private static int pickSaturation(Opt<ColorInfo> colorInfo, SaturationType saturationType, Luminosity luminosity) {
        return switch (saturationType) {
            case RANDOM -> randomWithin(new Range(0, 100));
            case MONOCHROME -> 0;
            case NATURAL -> {
                if (colorInfo.isEmpty())
                    yield 0;
                Range saturationRange = colorInfo.get().saturationRange();

                int min = saturationRange.start();
                int max = saturationRange.end();

                switch (luminosity) {
                    case LIGHT -> min = 55;
                    case BRIGHT -> min = max - 10;
                    case DARK -> max = 55;
                }
                yield randomWithin(new Range(min, max));
            }
        };
    }

    private static int pickBrightness(Opt<ColorInfo> colorInfo, int saturation, Luminosity luminosity) {
        int min = getMinimumBrightness(colorInfo, saturation);
        int max = 100;

        switch (luminosity) {
            case DARK -> max = min + 20;
            case LIGHT -> min = (max + min) / 2;
            case RANDOM -> min = 0;
        }
        return randomWithin(new Range(min, max));
    }

    private static int getMinimumBrightness(Opt<ColorInfo> colorInfo, int saturation) {
        if (colorInfo.isEmpty())
            return 0;

        List<Range> lowerBounds = colorInfo.get().lowerBounds();
        for (int i = 0; i < lowerBounds.size() - 1; i++) {

            int s1 = lowerBounds.get(i).start();
            int v1 = lowerBounds.get(i).end();

            if (i == lowerBounds.size() - 1)
                break;
            int s2 = lowerBounds.get(i + 1).start();
            int v2 = lowerBounds.get(i + 1).end();

            if (saturation >= s1 && saturation <= s2) {
                float m = (v2 - v1) / (float) (s2 - s1);
                float b = v1 - m * s1;
                return (int) (m * saturation + b);
            }
        }
        return 0;
    }

    private static Opt<ColorInfo> getColorInfo(int hue) {
        if (hue >= 334 && hue <= 360)
            hue -= 360;

        int hueF = hue;
        for (Shade key : COLORS.keySet()) {
            ColorInfo colorInfo = COLORS.get(key);
            if (colorInfo.hueRange().map(it -> it.contain(hueF)).orElse(false))
                return Opt.of(colorInfo);
        }
        return Opt.empty();
    }

    private static int randomWithin(Range range) {
        return (int) Math.floor(range.start() + RANDOM.nextDouble() * (range.end() + 1 - range.start()));
    }
}
