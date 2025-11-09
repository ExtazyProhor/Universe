package ru.prohor.universe.padawan.scripts.randcolor.api;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.padawan.scripts.randcolor.impl.ColorInfo;
import ru.prohor.universe.padawan.scripts.randcolor.impl.Range;

import java.util.ArrayList;
import java.util.List;

public enum Shade {
    MONOCHROME {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(0, 0));
            lowerBounds.add(new Range(100, 0));
            return defineColor(
                    Opt.empty(),
                    lowerBounds
            );
        }
    },
    RED {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(20, 100));
            lowerBounds.add(new Range(30, 92));
            lowerBounds.add(new Range(40, 89));
            lowerBounds.add(new Range(50, 85));
            lowerBounds.add(new Range(60, 78));
            lowerBounds.add(new Range(70, 70));
            lowerBounds.add(new Range(80, 60));
            lowerBounds.add(new Range(90, 55));
            lowerBounds.add(new Range(100, 50));
            return defineColor(
                    Opt.of(new Range(-26, 18)),
                    lowerBounds
            );
        }
    },
    ORANGE {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<Range>();
            lowerBounds.add(new Range(20, 100));
            lowerBounds.add(new Range(30, 93));
            lowerBounds.add(new Range(40, 88));
            lowerBounds.add(new Range(50, 86));
            lowerBounds.add(new Range(60, 85));
            lowerBounds.add(new Range(70, 70));
            lowerBounds.add(new Range(100, 70));
            return defineColor(
                    Opt.of(new Range(19, 46)),
                    lowerBounds
            );
        }
    },
    YELLOW {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(25, 100));
            lowerBounds.add(new Range(40, 94));
            lowerBounds.add(new Range(50, 89));
            lowerBounds.add(new Range(60, 86));
            lowerBounds.add(new Range(70, 84));
            lowerBounds.add(new Range(80, 82));
            lowerBounds.add(new Range(90, 80));
            lowerBounds.add(new Range(100, 75));

            return defineColor(
                    Opt.of(new Range(47, 62)),
                    lowerBounds
            );
        }
    },
    GREEN {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(30, 100));
            lowerBounds.add(new Range(40, 90));
            lowerBounds.add(new Range(50, 85));
            lowerBounds.add(new Range(60, 81));
            lowerBounds.add(new Range(70, 74));
            lowerBounds.add(new Range(80, 64));
            lowerBounds.add(new Range(90, 50));
            lowerBounds.add(new Range(100, 40));

            return defineColor(
                    Opt.of(new Range(63, 178)),
                    lowerBounds
            );

        }
    },
    BLUE {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(20, 100));
            lowerBounds.add(new Range(30, 86));
            lowerBounds.add(new Range(40, 80));
            lowerBounds.add(new Range(50, 74));
            lowerBounds.add(new Range(60, 60));
            lowerBounds.add(new Range(70, 52));
            lowerBounds.add(new Range(80, 44));
            lowerBounds.add(new Range(90, 39));
            lowerBounds.add(new Range(100, 35));

            return defineColor(
                    Opt.of(new Range(179, 257)),
                    lowerBounds
            );
        }
    },
    PURPLE {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(20, 100));
            lowerBounds.add(new Range(30, 87));
            lowerBounds.add(new Range(40, 79));
            lowerBounds.add(new Range(50, 70));
            lowerBounds.add(new Range(60, 65));
            lowerBounds.add(new Range(70, 59));
            lowerBounds.add(new Range(80, 52));
            lowerBounds.add(new Range(90, 45));
            lowerBounds.add(new Range(100, 42));

            return defineColor(
                    Opt.of(new Range(258, 282)),
                    lowerBounds
            );
        }
    },
    PINK {
        @Override
        public ColorInfo getColorInfo() {
            List<Range> lowerBounds = new ArrayList<>();
            lowerBounds.add(new Range(20, 100));
            lowerBounds.add(new Range(30, 90));
            lowerBounds.add(new Range(40, 86));
            lowerBounds.add(new Range(60, 84));
            lowerBounds.add(new Range(80, 80));
            lowerBounds.add(new Range(90, 75));
            lowerBounds.add(new Range(100, 73));

            return defineColor(
                    Opt.of(new Range(283, 334)),
                    lowerBounds
            );
        }
    };

    public abstract ColorInfo getColorInfo();

    private static ColorInfo defineColor(Opt<Range> hueRange, List<Range> lowerBounds) {
        int sMin = lowerBounds.getFirst().start();
        int sMax = lowerBounds.getLast().start();
        int bMin = lowerBounds.getLast().end();
        int bMax = lowerBounds.getFirst().end();

        return new ColorInfo(hueRange, new Range(sMin, sMax), new Range(bMin, bMax), lowerBounds);
    }
}
