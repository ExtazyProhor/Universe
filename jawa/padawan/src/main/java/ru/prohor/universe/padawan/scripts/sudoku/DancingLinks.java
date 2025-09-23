package ru.prohor.universe.padawan.scripts.sudoku;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DancingLinks implements SudokuSolver {
    private static final int BOARD_SIZE = 9;
    private static final int SUBSECTION_SIZE = 3;
    private static final int NO_VALUE = 0;
    private static final int CONSTRAINTS = 4;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;
    private static final int COVER_START_INDEX = 1;

    @Override
    public int[][] solve(int[][] board) {
        header = makeDLXBoard(initializeExactCoverBoard(board));
        int[][] result = runSolver();
        if (result == null)
            throw new RuntimeException("Переданное поле нерешаемо");
        return result;
    }

    private int getIndex(int row, int column, int num) {
        return (row - 1) * BOARD_SIZE * BOARD_SIZE + (column - 1) * BOARD_SIZE + (num - 1);
    }

    private boolean[][] createExactCoverBoard() {
        boolean[][] coverBoard = new boolean[BOARD_SIZE * BOARD_SIZE * MAX_VALUE][BOARD_SIZE * BOARD_SIZE * CONSTRAINTS];

        int hBase = 0;
        hBase = checkCellConstraint(coverBoard, hBase);
        hBase = checkRowConstraint(coverBoard, hBase);
        hBase = checkColumnConstraint(coverBoard, hBase);
        checkSubsectionConstraint(coverBoard, hBase);

        return coverBoard;
    }

    private void checkSubsectionConstraint(boolean[][] coverBoard, int hBase) {
        for (int row = COVER_START_INDEX; row <= BOARD_SIZE; row += SUBSECTION_SIZE) {
            for (int column = COVER_START_INDEX; column <= BOARD_SIZE; column += SUBSECTION_SIZE) {
                for (int n = COVER_START_INDEX; n <= BOARD_SIZE; n++, hBase++) {
                    for (int rowDelta = 0; rowDelta < SUBSECTION_SIZE; rowDelta++) {
                        for (int columnDelta = 0; columnDelta < SUBSECTION_SIZE; columnDelta++) {
                            int index = getIndex(row + rowDelta, column + columnDelta, n);
                            coverBoard[index][hBase] = true;
                        }
                    }
                }
            }
        }
    }

    private int checkColumnConstraint(boolean[][] coverBoard, int hBase) {
        for (int column = COVER_START_INDEX; column <= BOARD_SIZE; column++) {
            for (int n = COVER_START_INDEX; n <= BOARD_SIZE; n++, hBase++) {
                for (int row = COVER_START_INDEX; row <= BOARD_SIZE; row++) {
                    int index = getIndex(row, column, n);
                    coverBoard[index][hBase] = true;
                }
            }
        }
        return hBase;
    }

    private int checkRowConstraint(boolean[][] coverBoard, int hBase) {
        for (int row = COVER_START_INDEX; row <= BOARD_SIZE; row++) {
            for (int n = COVER_START_INDEX; n <= BOARD_SIZE; n++, hBase++) {
                for (int column = COVER_START_INDEX; column <= BOARD_SIZE; column++) {
                    int index = getIndex(row, column, n);
                    coverBoard[index][hBase] = true;
                }
            }
        }
        return hBase;
    }

    private int checkCellConstraint(boolean[][] coverBoard, int hBase) {
        for (int row = COVER_START_INDEX; row <= BOARD_SIZE; row++) {
            for (int column = COVER_START_INDEX; column <= BOARD_SIZE; column++, hBase++) {
                for (int n = COVER_START_INDEX; n <= BOARD_SIZE; n++) {
                    int index = getIndex(row, column, n);
                    coverBoard[index][hBase] = true;
                }
            }
        }
        return hBase;
    }

    private boolean[][] initializeExactCoverBoard(int[][] board) {
        boolean[][] coverBoard = createExactCoverBoard();
        for (int row = COVER_START_INDEX; row <= BOARD_SIZE; row++) {
            for (int column = COVER_START_INDEX; column <= BOARD_SIZE; column++) {
                int n = board[row - 1][column - 1];
                if (n != NO_VALUE) {
                    for (int num = MIN_VALUE; num <= MAX_VALUE; num++) {
                        if (num != n) {
                            Arrays.fill(coverBoard[getIndex(row, column, num)], false);
                        }
                    }
                }
            }
        }
        return coverBoard;
    }

    private ColumnNode header;
    private List<DancingNode> answer;

    private int[][] search(int k) {
        if (header.R == header) {
            return handleSolution(answer);
        } else {
            ColumnNode c = selectColumnNodeHeuristic();
            c.cover();

            for (DancingNode r = c.D; r != c; r = r.D) {
                answer.add(r);

                for (DancingNode j = r.R; j != r; j = j.R) {
                    j.C.cover();
                }

                search(k + 1);

                r = answer.removeLast();
                c = r.C;

                for (DancingNode j = r.L; j != r; j = j.L) {
                    j.C.uncover();
                }
            }
            c.uncover();
        }
        return null;
    }

    private ColumnNode selectColumnNodeHeuristic() {
        int min = Integer.MAX_VALUE;
        ColumnNode ret = null;
        for (ColumnNode c = (ColumnNode) header.R; c != header; c = (ColumnNode) c.R) {
            if (c.size < min) {
                min = c.size;
                ret = c;
            }
        }
        return ret;
    }

    private ColumnNode makeDLXBoard(boolean[][] grid) {
        final int COLS = grid[0].length;

        ColumnNode headerNode = new ColumnNode("header");
        List<ColumnNode> columnNodes = new ArrayList<>();

        for (int i = 0; i < COLS; i++) {
            ColumnNode n = new ColumnNode(Integer.toString(i));
            columnNodes.add(n);
            headerNode = (ColumnNode) headerNode.hookRight(n);
        }
        headerNode = headerNode.R.C;

        for (boolean[] aGrid : grid) {
            DancingNode prev = null;
            for (int j = 0; j < COLS; j++) {
                if (aGrid[j]) {
                    ColumnNode col = columnNodes.get(j);
                    DancingNode newNode = new DancingNode(col);
                    if (prev == null)
                        prev = newNode;
                    col.U.hookDown(newNode);
                    prev = prev.hookRight(newNode);
                    col.size++;
                }
            }
        }

        headerNode.size = COLS;

        return headerNode;
    }

    public int[][] runSolver() {
        answer = new LinkedList<>();
        return search(0);
    }

    private int[][] handleSolution(List<DancingNode> answer) {
        return parseBoard(answer);
    }

    private int[][] parseBoard(List<DancingNode> answer) {
        int size = 9;
        int[][] result = new int[size][size];
        for (DancingNode n : answer) {
            DancingNode rcNode = n;
            int min = Integer.parseInt(rcNode.C.name);
            for (DancingNode tmp = n.R; tmp != n; tmp = tmp.R) {
                int val = Integer.parseInt(tmp.C.name);
                if (val < min) {
                    min = val;
                    rcNode = tmp;
                }
            }
            int ans1 = Integer.parseInt(rcNode.C.name);
            int ans2 = Integer.parseInt(rcNode.R.C.name);
            int r = ans1 / size;
            int c = ans1 % size;
            int num = (ans2 % size) + 1;
            result[r][c] = num;
        }
        return result;
    }

    public static void printSolution(int[][] result) {
        int size = result.length;
        for (int[] aResult : result) {
            StringBuilder ret = new StringBuilder();
            for (int j = 0; j < size; j++) {
                ret.append(aResult[j]).append(" ");
            }
            System.out.println(ret);
        }
        System.out.println();
    }

    class ColumnNode extends DancingNode {
        int size;
        String name;

        ColumnNode(String n) {
            super();
            size = 0;
            name = n;
            C = this;
        }

        void cover() {
            unlinkLR();
            for (DancingNode i = this.D; i != this; i = i.D) {
                for (DancingNode j = i.R; j != i; j = j.R) {
                    j.unlinkUD();
                    j.C.size--;
                }
            }
        }

        void uncover() {
            for (DancingNode i = this.U; i != this; i = i.U) {
                for (DancingNode j = i.L; j != i; j = j.L) {
                    j.C.size++;
                    j.relinkUD();
                }
            }
            relinkLR();
        }
    }

    class DancingNode {
        DancingNode L, R, U, D;
        ColumnNode C;

        void hookDown(DancingNode node) {
            assert (this.C == node.C);
            node.D = this.D;
            node.D.U = node;
            node.U = this;
            this.D = node;
        }

        DancingNode hookRight(DancingNode node) {
            node.R = this.R;
            node.R.L = node;
            node.L = this;
            this.R = node;
            return node;
        }

        void unlinkLR() {
            this.L.R = this.R;
            this.R.L = this.L;
        }

        void relinkLR() {
            this.L.R = this.R.L = this;
        }

        void unlinkUD() {
            this.U.D = this.D;
            this.D.U = this.U;
        }

        void relinkUD() {
            this.U.D = this.D.U = this;
        }

        DancingNode() {
            L = R = U = D = this;
        }

        DancingNode(ColumnNode c) {
            this();
            C = c;
        }
    }
}
