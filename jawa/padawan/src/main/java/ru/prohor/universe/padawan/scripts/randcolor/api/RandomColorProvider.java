package ru.prohor.universe.padawan.scripts.randcolor.api;

import ru.prohor.universe.padawan.scripts.randcolor.impl.RandomColorProviderImpl;

import java.util.function.IntSupplier;

public interface RandomColorProvider {
    default int getColor() {
        return getColor(0, SaturationType.NATURAL, Luminosity.NORMAL);
    }

    default int getColor(Shade shade) {
        return getColor(shade, Luminosity.NORMAL);
    }

    int getColor(int hue, SaturationType saturationType, Luminosity luminosity);

    int getColor(Shade shade, Luminosity luminosity);

    default int[] getColors(int count) {
        return provideMany(this::getColor, count);
    }

    default int[] getColors(Shade shade, int count) {
        return provideMany(() -> getColor(shade), count);
    }

    default int[] getColors(int hue, SaturationType saturationType, Luminosity luminosity, int count) {
        return provideMany(() -> getColor(hue, saturationType, luminosity), count);
    }

    default int[] getColors(Shade shade, Luminosity luminosity, int count) {
        return provideMany(() -> getColor(shade, luminosity), count);
    }

    static RandomColorProvider getInstance() {
        return new RandomColorProviderImpl();
    }

    private static int[] provideMany(IntSupplier intSupplier, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("Count must be greater than zero");
        int[] colors = new int[count];
        for (int i = 0; i < count; ++i)
            colors[i] = intSupplier.getAsInt();
        return colors;
    }
}
