package ru.prohor.universe.padawan.scripts.sudoku;

import ru.prohor.universe.jocasta.core.collections.tuple.Tuple2;

import java.util.LinkedList;

/**
 * This is one of the first algorithms I wrote when I was learning Java in 2023.
 * In 2025, I simply replaced some structures with structures from jocasta-core,
 * added a solvability checker and inheritance from {@link SudokuSolver}
 */
public class MySolver implements SudokuSolver {
    private boolean[][] rows;
    private boolean[][] columns;
    private boolean[][] squares;

    private void solveInternal(int[][] field) {
        rows = new boolean[9][9];
        columns = new boolean[9][9];
        squares = new boolean[9][9];

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (field[i][j] == 0)
                    continue;
                rows[i][field[i][j] - 1] = true;
                columns[j][field[i][j] - 1] = true;
                squares[i / 3 * 3 + j / 3][field[i][j] - 1] = true;
            }
        }

        LinkedList<Tuple2<Integer, Integer>> stack = new LinkedList<>();
        boolean back = false;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                int newValue;
                if (!back) {
                    if (field[i][j] != 0)
                        continue;
                    stack.addLast(new Tuple2<>(i, j));
                    newValue = getNew(i, j, field[i][j]);
                } else {
                    back = false;
                    newValue = getNext(i, j, field[i][j]);
                }
                if (newValue == -1) {
                    field[i][j] = 0;
                    stack.removeLast();
                    Tuple2<Integer, Integer> pair = stack.getLast();
                    i = pair.get1();
                    j = pair.get2() - 1;
                    back = true;
                } else {
                    field[i][j] = newValue;
                }
            }
        }
    }

    @Override
    public int[][] solve(int[][] field) {
        try {
            solveInternal(field);
            return field;
        } catch (Exception e) {
            throw new RuntimeException("Переданное поле нерешаемо");
        }
    }

    private int getNew(int i, int j, int value) {
        int squareIndex = i / 3 * 3 + j / 3;
        for (int k = value; k < 9; ++k) {
            if (!rows[i][k] && !columns[j][k] && !squares[squareIndex][k]) {
                rows[i][k] = true;
                columns[j][k] = true;
                squares[squareIndex][k] = true;
                return k + 1;
            }
        }
        return -1;
    }

    private int getNext(int i, int j, int value) {
        rows[i][value - 1] = false;
        columns[j][value - 1] = false;
        squares[i / 3 * 3 + j / 3][value - 1] = false;
        return getNew(i, j, value);
    }
}
