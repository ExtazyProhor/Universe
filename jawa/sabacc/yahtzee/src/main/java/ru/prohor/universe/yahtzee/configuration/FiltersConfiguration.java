package ru.prohor.universe.yahtzee.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.yahtzee.services.AccountService;
import ru.prohor.universe.yahtzee.web.filters.YahtzeeAuthorizedUserFilter;

@Configuration
public class FiltersConfiguration {
    @Bean
    public FilterRegistrationBean<YahtzeeAuthorizedUserFilter> yahtzeeAuthorizedUserFilterRegistration(
            AccountService accountService
    ) {
        FilterRegistrationBean<YahtzeeAuthorizedUserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new YahtzeeAuthorizedUserFilter(accountService));
        registration.addUrlPatterns("/*");
        registration.setOrder(YahtzeeAuthorizedUserFilter.YAHTZEE_AUTHORIZED_USER_FILTER_ORDER);
        return registration;
    }
}
