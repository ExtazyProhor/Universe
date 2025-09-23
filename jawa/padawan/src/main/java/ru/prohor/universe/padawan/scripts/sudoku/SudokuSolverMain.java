package ru.prohor.universe.padawan.scripts.sudoku;

import java.util.Arrays;

public class SudokuSolverMain {
    public static void main(String[] args) {
        int[][] result;

        SudokuSolver my = new MySolver();
        result = my.solve(copyField(EASY_GAME));
        printField(result);
        result = my.solve(copyField(HARD_GAME));
        printField(result);
        result = my.solve(copyField(THEIR_GAME));
        printField(result);

        SudokuSolver site = new SiteSolver();
        result = site.solve(copyField(EASY_GAME));
        printField(result);
        result = site.solve(copyField(HARD_GAME));
        printField(result);
        result = site.solve(copyField(THEIR_GAME));
        printField(result);
    }

    private static final int[][] EASY_GAME = {
            {0, 0, 0, 4, 0, 8, 9, 7, 6},
            {4, 7, 8, 3, 6, 9, 2, 5, 1},
            {0, 0, 2, 5, 7, 0, 8, 3, 0},
            {0, 1, 6, 2, 9, 0, 0, 8, 0},
            {8, 3, 5, 0, 4, 6, 7, 2, 9},
            {0, 4, 9, 0, 0, 3, 6, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 6, 7},
            {0, 8, 0, 0, 0, 7, 1, 9, 2},
            {0, 0, 7, 9, 1, 0, 3, 4, 8}
    };
    private static final int[][] HARD_GAME = {
            {0, 0, 0, 0, 0, 0, 1, 0, 9},
            {6, 0, 0, 0, 5, 0, 0, 0, 0},
            {0, 0, 0, 0, 8, 0, 7, 0, 0},
            {0, 0, 0, 1, 0, 9, 0, 0, 0},
            {5, 0, 0, 0, 0, 3, 0, 0, 0},
            {0, 0, 0, 7, 0, 0, 0, 0, 0},
            {0, 1, 7, 0, 0, 0, 0, 0, 0},
            {0, 2, 0, 0, 0, 0, 0, 6, 0},
            {0, 0, 9, 0, 0, 0, 0, 8, 0}
    };
    private static final int[][] THEIR_GAME = {
            {8, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 3, 6, 0, 0, 0, 0, 0},
            {0, 7, 0, 0, 9, 0, 2, 0, 0},
            {0, 5, 0, 0, 0, 7, 0, 0, 0},
            {0, 0, 0, 0, 4, 5, 7, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 3, 0},
            {0, 0, 1, 0, 0, 0, 0, 6, 8},
            {0, 0, 8, 5, 0, 0, 0, 1, 0},
            {0, 9, 0, 0, 0, 0, 4, 0, 0}
    };


    private static void printField(int[][] field) {
        System.out.println("\n\n");
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (field[i][j] != 0)
                    System.out.print(field[i][j]);
                else
                    System.out.print(' ');
                System.out.print(' ');
                if (j == 2 || j == 5)
                    System.out.print('|');
                System.out.print(' ');
            }
            System.out.println();
            if (i == 2 || i == 5)
                System.out.println("_ ".repeat(14));
        }
        System.out.println("\n\n");
    }

    private static int[][] copyField(int[][] field) {
        int[][] newMatrix = new int[field.length][];
        for (int i = 0; i < field.length; ++i)
            newMatrix[i] = Arrays.copyOf(field[i], field[i].length);
        return newMatrix;
    }
}
