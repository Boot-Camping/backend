package com.github.project3.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.github.project3.repository.user", "com.github.project3.repository.mypage"
        , "com.github.project3.repository.book", "com.github.project3.repository.bookDate", "com.github.project3.repository.camp"
        , "com.github.project3.repository.cash", "com.github.project3.repository.review","com.github.project3.repository.admin"
        , "com.github.project3.repository.reply"},
        entityManagerFactoryRef = "entityManagerFactoryBean1"
)
public class JpaConfig {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean1(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.github.project3.entity.user",
                "com.github.project3.entity.book",
                "com.github.project3.entity.camp",
                "com.github.project3.entity.notice",
                "com.github.project3.entity.review",
                "com.github.project3.entity.wishlist",
                "com.github.project3.entity.reply"
        );

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comment", "true");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        dataSource.setUrl(dataSourceConfig.getUrl());
        dataSource.setUsername(dataSourceConfig.getUsername());
        dataSource.setPassword(dataSourceConfig.getPassword());
        return dataSource;
    }
}
