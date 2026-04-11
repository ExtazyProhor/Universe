package ru.prohor.universe.padawan.tasks.interview.buffer;

public interface TextEditorBuffer {
    /**
     * добавляет строку {@code s} в конец буфера
     */
    void append(String s);

    /**
     * удаляет {@code n} последних символов из буфера
     */
    void deleteLastChars(int n);

    /**
     * отменяет последнюю операцию. Отменяет любую операцию, кроме этой
     */
    void undoLastOperation();

    /**
     * вывод текста всего буфера
     */
    void printContent();

    /**
     * вывод последнего символа буфера
     */
    void printLastChar();
}
