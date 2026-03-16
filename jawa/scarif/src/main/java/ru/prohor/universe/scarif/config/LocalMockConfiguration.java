package ru.prohor.universe.scarif.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.scarif.data.user.User;
import ru.prohor.universe.scarif.services.UserService;

@Configuration
@Profile("local")
public class LocalMockConfiguration {
    @Bean
    public User addAdmin(
            UserService userService
    ) {
        return userService.createUser("admin", "admin@gmail.com", "adminADMIN");
    }
}
