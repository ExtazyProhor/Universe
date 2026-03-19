package ru.prohor.universe.yahtzee.app.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MenuInitialResponse(
        String name,
        String username,
        ColorInfo color,
        @JsonProperty("image_id")
        String imageId,
        GeneralRoomInfo room, // optional
        @JsonProperty("has_notifications")
        boolean hasNotifications
) {}
