package ru.prohor.universe.yahtzee.app.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.core.core.color.TeamColor;

public record ColorInfo(
        @JsonProperty("random_color")
        boolean randomColor, // if true, color and contrast are absent
        String color, // format: "ff0000", hex, without alpha
        String contrast // format: "ff0000", hex, without alpha,
) {
    public static ColorInfo of(Opt<TeamColor> color) {
        return new ColorInfo(
                color.isEmpty(),
                color.isPresent() ? color.get().background() : null,
                color.isPresent() ? color.get().text() : null
        );
    }
}
