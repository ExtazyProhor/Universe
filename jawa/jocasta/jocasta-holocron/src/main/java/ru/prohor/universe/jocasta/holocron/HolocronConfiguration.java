package ru.prohor.universe.jocasta.holocron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HolocronConfiguration {
    @Bean
    public static BeanFactoryPostProcessor holocronPropertiesResolver(
            @Value("${universe.holocron.file-path}") String filePath
    ) {
        return new HolocronPropertiesResolver(filePath);
    }
}
