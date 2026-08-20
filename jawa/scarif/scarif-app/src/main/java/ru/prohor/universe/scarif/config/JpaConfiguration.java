package ru.prohor.universe.scarif.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import ru.prohor.universe.scarif.data.refresh.JpaRefreshTokenMethods;
import ru.prohor.universe.scarif.data.session.JpaSessionMethods;
import ru.prohor.universe.scarif.data.user.JpaUsersMethods;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackageClasses = {
        JpaUsersMethods.class,
        JpaRefreshTokenMethods.class,
        JpaSessionMethods.class,
})
public class JpaConfiguration {
    @Bean
    public DataSource dataSource(
            @Value("${universe.scarif.datasource.url}") String url,
            @Value("${universe.scarif.datasource.driver}") String driver,
            @Value("${universe.scarif.datasource.username}") String username,
            @Value("${universe.scarif.datasource.password}") String password,
            @Value("${universe.scarif.datasource.maximum-pool-size}") int maximumPoolSize,
            @Value("${universe.scarif.datasource.minimum-idle}") int minimumIdle,
            @Value("${universe.scarif.datasource.connection-timeout}") int connectionTimeout,
            @Value("${universe.scarif.datasource.idle-timeout}") int idleTimeout,
            @Value("${universe.scarif.datasource.max-lifetime}") int maxLifetime,
            @Value("${universe.scarif.datasource.pool-name}") String poolName
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setMaxLifetime(maxLifetime);
        dataSource.setPoolName(poolName);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Value("${universe.scarif.jpa.show-sql}") String showSql,
            @Value("${universe.scarif.jpa.ddl-auto}") String ddlAuto,
            DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan("ru.prohor.universe.scarif.data");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManager.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        entityManager.setJpaProperties(properties);
        return entityManager;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
