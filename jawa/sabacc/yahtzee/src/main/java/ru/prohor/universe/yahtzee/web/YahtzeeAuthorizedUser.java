package ru.prohor.universe.yahtzee.web;

import org.bson.types.ObjectId;

import java.util.UUID;

public record YahtzeeAuthorizedUser(
        long id,
        UUID uuid,
        ObjectId objectId,
        String username,
        String name,
        ObjectId imageId,
        String color
) {
    public static final String ATTRIBUTE_KEY = "universe.yahtzee-authorized-user";
}
