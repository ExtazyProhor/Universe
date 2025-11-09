package ru.prohor.universe.padawan.scripts.randcolor.impl;

import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

public record ColorInfo(
        Opt<Range> hueRange,
        Range saturationRange,
        Range brightnessRange,
        List<Range> lowerBounds
) {}
