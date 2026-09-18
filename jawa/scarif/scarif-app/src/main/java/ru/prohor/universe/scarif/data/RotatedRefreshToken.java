package ru.prohor.universe.scarif.data;

import org.bson.types.ObjectId;

import java.time.Instant;

public record RotatedRefreshToken(ObjectId id, Instant rotatedAt) {}
