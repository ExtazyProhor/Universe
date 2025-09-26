package ru.prohor.universe.yoda.data.entities.pojo;

import org.bson.types.ObjectId;

public record Tutor(
        ObjectId objectId,
        String name,
        String surname
) {}
