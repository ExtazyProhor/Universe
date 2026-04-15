package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class UrlHandlerFilterConfiguration {
    @Bean
    public UrlHandlerFilter trailingSlashFilter() {
        return UrlHandlerFilter
                .trailingSlashHandler("/**")
                .redirect(HttpStatus.MOVED_PERMANENTLY)
                .build();
    }
}
