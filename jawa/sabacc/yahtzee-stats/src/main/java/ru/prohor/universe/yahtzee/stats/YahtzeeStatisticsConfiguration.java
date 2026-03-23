package ru.prohor.universe.yahtzee.stats;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
public class YahtzeeStatisticsConfiguration {
    @Configuration
    @Profile("local | testing")
    @Import(LocalStatisticsCalculationService.class)
    public static class LocalYahtzeeStatisticsConfiguration {}

    @Configuration
    @Profile("stable | canary")
    @Import(MorphiaStatisticsCalculationService.class)
    public static class MorphiaYahtzeeStatisticsConfiguration {}
}
