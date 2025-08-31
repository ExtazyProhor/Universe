package ru.prohor.universe.yahtzee.data;

public interface MongoEntityPojo<T> {
    T toDto();
}
