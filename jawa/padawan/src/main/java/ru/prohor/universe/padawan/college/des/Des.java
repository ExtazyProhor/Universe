package ru.prohor.universe.padawan.college.des;

public interface Des {
    long generateKey();

    String encode(String message, long key);

    String decode(String encodedMessage, long key);

    static Des getInstance() {
        return new DesImpl();
    }
}
