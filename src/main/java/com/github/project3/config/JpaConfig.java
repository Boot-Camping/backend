package com.github.project3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
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
