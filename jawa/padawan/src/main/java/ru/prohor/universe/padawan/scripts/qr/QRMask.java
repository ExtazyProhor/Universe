package ru.prohor.universe.padawan.scripts.qr;

public enum QRMask {
    MASK_O {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return (column + row) % 2 == 0;
        }
    },
    MASK_1 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return row % 2 == 0;
        }
    },
    MASK_2 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return column % 3 == 0;
        }
    },
    MASK_3 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return (column + row) % 3 == 0;
        }
    },
    MASK_4 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return (column / 3 + row / 2) % 2 == 0;
        }
    },
    MASK_5 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return column * row % 6 == 0;
        }
    },
    MASK_6 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return (column * row % 2 + column * row % 3) % 2 == 0;
        }
    },
    MASK_7 {
        @Override
        public boolean isNeedToInverse(int column, int row) {
            return (column * row % 3 + (column + row) % 2) % 2 == 0;
        }
    };

    abstract boolean isNeedToInverse(int column, int row);
}
