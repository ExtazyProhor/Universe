package ru.prohor.universe.yahtzee.core.services;

import org.bson.types.ObjectId;
import ru.prohor.universe.yahtzee.core.web.exceptions.InvalidObjectIdException;

public class YahtzeeUtils {
    public static ObjectId parseObjectId(String objectId) {
        try {
            return new ObjectId(objectId);
        } catch (Exception e) {
            e.printStackTrace(); // TODO log (SB - Suspicious Behaviour)
            throw new InvalidObjectIdException(objectId, e);
        }
    }
}
