package ru.prohor.universe.yahtzee.core.core.color;

import java.util.Arrays;
import java.util.Random;

public class RandomColor implements SelectedColor {
    private final Random random = new Random();

    private final int[] actualColors;

    RandomColor(YahtzeeColor[] yahtzeeColors) {
        this.actualColors = Arrays.stream(yahtzeeColors)
                .mapToInt(YahtzeeColor::getColorId)
                .toArray();
    }

    @Override
    public int getColorId() {
        return actualColors[random.nextInt(actualColors.length)];
    }
}
