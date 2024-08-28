package com.github.project3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.github.project3.repository.user", "com.github.project3.repository.mypage"
        , "com.github.project3.repository.book", "com.github.project3.repository.bookDate", "com.github.project3.repository.camp"
        , "com.github.project3.repository.cash", "com.github.project3.repository.review","com.github.project3.repository.admin"
        , "com.github.project3.repository.reply", "com.github.project3.repository.chat"}
)

public class JpaConfig {
    @Autowired
    private DataSourceConfig dataSourceConfig;

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
