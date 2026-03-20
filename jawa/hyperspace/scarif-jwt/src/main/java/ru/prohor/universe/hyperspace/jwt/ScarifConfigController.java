package ru.prohor.universe.hyperspace.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scarif/config")
public class ScarifConfigController {
    private final ScarifConfig config;

    public ScarifConfigController(
            @Value("${universe.scarif.jwt.refresh-url}") String refreshUrl
    ) {
        this.config = new ScarifConfig(refreshUrl);
    }

    @GetMapping
    public ScarifConfig getConfig() {
        return config;
    }

    public record ScarifConfig(
            @JsonProperty("refresh_url")
            String refreshUrl
    ) {}
}
