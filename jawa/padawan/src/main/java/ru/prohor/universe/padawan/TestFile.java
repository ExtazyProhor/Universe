package ru.prohor.universe.padawan;

public enum TestFile {
    CSS("css.css"),
    HTML("html.html"),
    INPUT("input.txt"),
    JAVA("java.java"),
    JS("js.js"),
    JSON("json.json"),
    MD("md.md"),
    OUTPUT("output.txt"),
    TXT("txt.txt");

    public final String file;

    TestFile(String file) {
        this.file = file;
    }
}
