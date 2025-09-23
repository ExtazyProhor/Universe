package ru.prohor.universe.jocasta.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.spring.features.HolocronPropertiesResolver;

@Configuration
public class HolocronConfiguration {
    @Bean
    public static BeanFactoryPostProcessor holocronPropertiesResolver(
            @Value("${universe.holocron.file-path}") String filePath
    ) {
        return new HolocronPropertiesResolver(filePath);
    }
}
