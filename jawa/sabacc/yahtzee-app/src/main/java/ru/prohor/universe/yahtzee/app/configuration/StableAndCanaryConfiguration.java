package ru.prohor.universe.yahtzee.app.configuration;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.prohor.universe.yahtzee.app.services.AdminValidationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Profile("stable | canary")
public class StableAndCanaryConfiguration {
    @Bean
    public AdminValidationService adminValidationService(
            @Value("${universe.yahtzee.admin-ids}") List<String> adminIds
    ) {
        Set<ObjectId> adminIdsSet = adminIds.stream().map(ObjectId::new).collect(Collectors.toSet());
        return player -> adminIdsSet.contains(player.id());
    }
}
