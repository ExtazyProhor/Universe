package ru.prohor.universe.yahtzee.services.color;

import java.util.Arrays;

public class HungarianAlgorithm {
    private final int[][] costMatrix;
    private final int rows, cols, dim;
    private final int[] labelByWorker, labelByJob;
    private final int[] minSlackWorkerByJob;
    private final int[] minSlackValueByJob;
    private final int[] matchJobByWorker, matchWorkerByJob;
    private final int[] parentWorkerByCommittedJob;
    private final boolean[] committedWorkers;

    public HungarianAlgorithm(int[][] costMatrix) {
        this.rows = costMatrix.length;
        this.cols = costMatrix[0].length;
        this.dim = Math.max(rows, cols);
        this.costMatrix = new int[this.dim][this.dim];

        for (int w = 0; w < this.dim; w++) {
            if (w < costMatrix.length) {
                if (costMatrix[w].length != cols) {
                    throw new IllegalArgumentException("Irregular cost matrix");
                }
                this.costMatrix[w] = Arrays.copyOf(costMatrix[w], this.dim);
            } else {
                this.costMatrix[w] = new int[this.dim];
            }
        }

        labelByWorker = new int[dim];
        labelByJob = new int[dim];
        minSlackWorkerByJob = new int[dim];
        minSlackValueByJob = new int[dim];
        committedWorkers = new boolean[dim];
        parentWorkerByCommittedJob = new int[dim];
        matchJobByWorker = new int[dim];
        Arrays.fill(matchJobByWorker, -1);
        matchWorkerByJob = new int[dim];
        Arrays.fill(matchWorkerByJob, -1);
    }

    public int[] execute() {
        reduce();
        computeInitialFeasibleSolution();
        greedyMatch();
        int w = fetchUnmatchedWorker();
        while (w < dim) {
            initializePhase(w);
            executePhase();
            w = fetchUnmatchedWorker();
        }
        int[] result = Arrays.copyOf(matchJobByWorker, rows);
        for (w = 0; w < result.length; w++)
            if (result[w] >= cols)
                result[w] = -1;
        return result;
    }

    private void reduce() {
        for (int w = 0; w < dim; w++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < dim; j++)
                if (costMatrix[w][j] < min)
                    min = costMatrix[w][j];
            for (int j = 0; j < dim; j++)
                costMatrix[w][j] -= min;
        }
        int[] min = new int[dim];
        for (int j = 0; j < dim; j++)
            min[j] = Integer.MAX_VALUE;
        for (int w = 0; w < dim; w++)
            for (int j = 0; j < dim; j++)
                if (costMatrix[w][j] < min[j])
                    min[j] = costMatrix[w][j];
        for (int w = 0; w < dim; w++)
            for (int j = 0; j < dim; j++)
                costMatrix[w][j] -= min[j];
    }

    private void computeInitialFeasibleSolution() {
        for (int j = 0; j < dim; j++)
            labelByJob[j] = Integer.MAX_VALUE;
        for (int w = 0; w < dim; w++)
            for (int j = 0; j < dim; j++)
                if (costMatrix[w][j] < labelByJob[j])
                    labelByJob[j] = costMatrix[w][j];
    }

    private void greedyMatch() {
        for (int w = 0; w < dim; w++)
            for (int j = 0; j < dim; j++)
                if (matchJobByWorker[w] == -1 && matchWorkerByJob[j] == -1
                        && costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0)
                    match(w, j);
    }

    private void initializePhase(int w) {
        Arrays.fill(committedWorkers, false);
        Arrays.fill(parentWorkerByCommittedJob, -1);
        committedWorkers[w] = true;
        for (int j = 0; j < dim; j++) {
            minSlackValueByJob[j] = costMatrix[w][j] - labelByWorker[w] - labelByJob[j];
            minSlackWorkerByJob[j] = w;
        }
    }

    private void executePhase() {
        while (true) {
            int minSlackWorker = -1, minSlackJob = -1;
            int minSlackValue = Integer.MAX_VALUE;
            for (int j = 0; j < dim; j++) {
                if (parentWorkerByCommittedJob[j] == -1) {
                    if (minSlackValueByJob[j] < minSlackValue) {
                        minSlackValue = minSlackValueByJob[j];
                        minSlackWorker = minSlackWorkerByJob[j];
                        minSlackJob = j;
                    }
                }
            }
            if (minSlackValue > 0)
                updateLabeling(minSlackValue);
            parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
            if (matchWorkerByJob[minSlackJob] == -1) {
                int committedJob = minSlackJob;
                int parentWorker = parentWorkerByCommittedJob[committedJob];
                while (true) {
                    int temp = matchJobByWorker[parentWorker];
                    match(parentWorker, committedJob);
                    committedJob = temp;
                    if (committedJob == -1) {
                        break;
                    }
                    parentWorker = parentWorkerByCommittedJob[committedJob];
                }
                return;
            }
            int worker = matchWorkerByJob[minSlackJob];
            committedWorkers[worker] = true;
            for (int j = 0; j < dim; j++) {
                if (parentWorkerByCommittedJob[j] == -1) {
                    int slack = costMatrix[worker][j] - labelByWorker[worker] - labelByJob[j];
                    if (minSlackValueByJob[j] > slack) {
                        minSlackValueByJob[j] = slack;
                        minSlackWorkerByJob[j] = worker;
                    }
                }
            }
        }
    }

    private void updateLabeling(int slack) {
        for (int w = 0; w < dim; w++)
            if (committedWorkers[w])
                labelByWorker[w] += slack;
        for (int j = 0; j < dim; j++)
            if (parentWorkerByCommittedJob[j] != -1)
                labelByJob[j] -= slack;
            else
                minSlackValueByJob[j] -= slack;
    }

    private void match(int w, int j) {
        matchJobByWorker[w] = j;
        matchWorkerByJob[j] = w;
    }

    private int fetchUnmatchedWorker() {
        for (int w = 0; w < dim; w++)
            if (matchJobByWorker[w] == -1)
                return w;
        return dim;
    }
}
