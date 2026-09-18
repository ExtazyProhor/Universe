package ru.prohor.universe.scarif.web.api;

import org.bson.types.ObjectId;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

public record SessionDescription(String ip, Opt<String> userAgent, ObjectId id, String createdAt, boolean current) {}
