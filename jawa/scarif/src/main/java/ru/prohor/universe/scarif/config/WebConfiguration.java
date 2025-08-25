package ru.prohor.universe.scarif.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final String rootOrigin;
    private final String subOrigins;

    public WebConfiguration(
            @Value("${universe.root-origin}") String rootOrigin,
            @Value("${universe.sub-origins}") String subOrigins
    ) {
        this.rootOrigin = rootOrigin;
        this.subOrigins = subOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(rootOrigin, subOrigins)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
