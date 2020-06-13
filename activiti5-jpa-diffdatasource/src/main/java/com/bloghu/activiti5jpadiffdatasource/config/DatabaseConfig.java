package com.bloghu.activiti5jpadiffdatasource.config;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class DatabaseConfig {

	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(
			entityManagerFactoryRef = "activitiEntityManagerFactory", 
			basePackages = { "com.bloghu.activiti5jpadiffdatasource.activiti" })
	public static class ActivitiDatabaseConfig extends AbstractProcessEngineAutoConfiguration{

		@Bean(name = "activitiDataSource")
		@ConfigurationProperties(prefix = "activiti.spring.datasource")
		public DataSource activitiDataSource() {
			return DataSourceBuilder.create().build();
		}

		@Bean(name = "activitiEntityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("activitiDataSource") DataSource dataSource) {
			JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
			
			LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	        emf.setDataSource(dataSource);
	        emf.setJpaVendorAdapter(jpaVendorAdapter);
	        emf.setPackagesToScan("com.bloghu.activiti5jpadiffdatasource.activiti");
	        emf.setPersistenceUnitName("activiti");
	        emf.afterPropertiesSet();
	        return emf;
		}

		@Bean(name = "activitiTransactionManager")
		public PlatformTransactionManager activitiTransactionManager(@Qualifier("activitiEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
			return new JpaTransactionManager(entityManagerFactory);
		}
		
	}
	
	@Configuration
	@EnableConfigurationProperties(ActivitiProperties.class)
	public static class DataSourceProcessEngineConfiguration extends AbstractProcessEngineAutoConfiguration {

	    @Bean
	    @ConditionalOnMissingBean
	    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
	    		@Qualifier("activitiDataSource") DataSource activitiDataSource,
	    		@Qualifier("activitiTransactionManager") PlatformTransactionManager activitiTransactionManager,
	            SpringAsyncExecutor springAsyncExecutor) throws IOException {
	      
	      return this.baseSpringProcessEngineConfiguration(activitiDataSource, activitiTransactionManager, springAsyncExecutor);
	    }
	}

	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(
			entityManagerFactoryRef = "entityManagerFactory", 
			basePackages = { "com.bloghu.activiti5jpadiffdatasource.tesseract" })
	public static class TesseractDatabaseConfig {
		
		@Primary
		@Bean(name = "dataSource")
		@ConfigurationProperties(prefix = "spring.datasource")
		public DataSource dataSource() {
			return DataSourceBuilder.create().build();
		}

		@Primary
		@Bean(name = "entityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
			
			JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
			
			LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	        emf.setDataSource(dataSource);
	        emf.setJpaVendorAdapter(jpaVendorAdapter);
	        emf.setPackagesToScan("com.bloghu.activiti5jpadiffdatasource.tesseract");
	        emf.setPersistenceUnitName("tesseract");
	        emf.afterPropertiesSet();
	        return emf;
		}

		@Primary
		@Bean(name = "transactionManager")
		public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
			
			return new JpaTransactionManager(entityManagerFactory);
		}
		
	}

}
