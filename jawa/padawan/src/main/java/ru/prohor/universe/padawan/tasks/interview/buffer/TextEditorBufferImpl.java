package ru.prohor.universe.padawan.tasks.interview.buffer;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class TextEditorBufferImpl implements TextEditorBuffer {
    private static final int MAX_HISTORY_LENGTH = 30;

    private final Deque<Runnable> history;
    private final PrintStream printStream;
    private final StringBuilder builder;

    public TextEditorBufferImpl(PrintStream printStream) {
        this.history = new ArrayDeque<>();
        this.printStream = printStream;
        this.builder = new StringBuilder();
    }

    @Override
    public void append(String s) {
        int length = builder.length();
        builder.append(s);
        addUndoAction(() -> builder.setLength(length));
    }

    @Override
    public void deleteLastChars(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("count of chars can not be negative");
        }
        if (n > builder.length()) {
            throw new IllegalArgumentException("count of chars less than buffer length");
        }
        int newEndIndex = builder.length() - n;
        String removed = builder.substring(newEndIndex);
        builder.setLength(newEndIndex);
        addUndoAction(() -> builder.append(removed));
    }

    @Override
    public void undoLastOperation() {
        if (history.isEmpty()) {
            throw new IllegalStateException("there is no last action");
        }
        history.pollLast().run();
    }

    @Override
    public void printContent() {
        printStream.println(builder);
    }

    @Override
    public void printLastChar() {
        if (builder.isEmpty()) {
            throw new IllegalStateException("buffer is empty");
        }
        printStream.print(builder.charAt(builder.length() - 1));
    }

    private void addUndoAction(Runnable undoAction) {
        if (history.size() >= MAX_HISTORY_LENGTH) {
            history.pollFirst();
        }
        history.addLast(undoAction);
    }
}
