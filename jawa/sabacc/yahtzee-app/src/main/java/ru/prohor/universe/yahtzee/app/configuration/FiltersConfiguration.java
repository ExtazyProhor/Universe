package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.yahtzee.app.services.AccountService;
import ru.prohor.universe.yahtzee.app.web.filters.YahtzeeAuthorizedUserFilter;

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
