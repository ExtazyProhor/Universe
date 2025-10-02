package ru.prohor.universe.yoda.data.entities.pojo;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record Lesson(
        ObjectId objectId,
        Instant start,
        ObjectId tutor,
        ObjectId student,
        Opt<Integer> mark
) {}
