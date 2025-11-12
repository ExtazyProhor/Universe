package ru.prohor.universe.padawan.scripts.money;

import java.util.List;

public record Operation(
        Person paying,
        List<Person> debtors,
        int value
) {}
