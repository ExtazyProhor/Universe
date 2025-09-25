package ru.prohor.universe.yoda.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfiguration {
    @Bean
    public FileLogger fileLogger(@Value("${yoda.logfile.path}") String path) {
        return new FileLogger(path);
    }
}
