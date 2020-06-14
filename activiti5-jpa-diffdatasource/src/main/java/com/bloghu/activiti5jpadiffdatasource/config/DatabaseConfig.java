package com.bloghu.activiti5jpadiffdatasource.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bloghu.activiti5jpadiffdatasource.tesseract.service.CityRepository;

@Configuration
public class DatabaseConfig {

	@Autowired
	@Qualifier("dataSource") 
	private DataSource dataSource;

	@Autowired
	private CityRepository cityRepository;
	
	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(
			entityManagerFactoryRef = "activitiEntityManagerFactory", 
			basePackages = { "com.bloghu.activiti5jpadiffdatasource.activiti" })
	public static class ActivitiDatabaseConfig extends AbstractProcessEngineAutoConfiguration{

		@Bean(name = "activitiDataSource")
		@ConfigurationProperties(prefix = "activiti.spring.datasource")
		public DataSource activitiDataSource() {
			return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").build();
		}

		@Bean(name = "activitiEntityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("activitiDataSource") DataSource dataSource) {
			HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
			jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
			
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
		
		@Autowired
		private Environment env;
		
		@Primary
		@Bean(name = "dataSource")
		@ConfigurationProperties(prefix = "spring.datasource")
		public DataSource dataSource() {
			return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").build();
		}

		@Primary
		@Bean(name = "entityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
			
			Properties properties = new Properties();
			properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
			properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
			properties.setProperty("hibernate.current_session_context_class", env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
			properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", env.getProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation"));
			properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
			properties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
			
			HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
			jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
			
			LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	        emf.setDataSource(dataSource);
	        emf.setJpaVendorAdapter(jpaVendorAdapter);
	        emf.setPackagesToScan("com.bloghu.activiti5jpadiffdatasource.tesseract");
	        emf.setPersistenceUnitName("tesseract");
	        emf.setJpaProperties(properties);
	        emf.afterPropertiesSet();
	        return emf;
		}

		@Primary
		@Bean(name = "transactionManager")
		public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
			
			return new JpaTransactionManager(entityManagerFactory);
		}
		
	}
	
	@PostConstruct
	protected void initialize() {
		long total = cityRepository.count();
		
		if(total == 0) {
			Resource initSchema = new ClassPathResource("import.sql");
			ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		    databasePopulator.addScript(initSchema);
		    databasePopulator.setContinueOnError(true);
		    DatabasePopulatorUtils.execute(databasePopulator, dataSource);
		}
	}

}
