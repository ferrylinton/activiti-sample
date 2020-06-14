package com.bloghu.activiti5jpadiffdatasource.config;

import java.io.IOException;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig extends AbstractProcessEngineAutoConfiguration{

	@Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(SpringAsyncExecutor springAsyncExecutor) throws IOException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres?currentSchema=activiti");
        config.setUsername("postgres");
        config.setPassword("password");
        
        HikariDataSource dataSource = new HikariDataSource(config);

        return this.baseSpringProcessEngineConfiguration(dataSource, new DataSourceTransactionManager(dataSource), springAsyncExecutor);
	}

}
