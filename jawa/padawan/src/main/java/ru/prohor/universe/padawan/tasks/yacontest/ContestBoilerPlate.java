package ru.prohor.universe.padawan.tasks.yacontest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@SuppressWarnings({"EmptyTryBlock", "unused"})
public interface ContestBoilerPlate {
    private static void ReaderWriterFiles() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))
        ) {

        }
    }

    private static void ReaderWriterStdInOut() throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))
        ) {

        }
    }
}
