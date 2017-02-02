package com.compomics.pladipus.repository.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.impl.DefaultDAOImpl;
import com.compomics.pladipus.repository.dao.impl.UserDAO;
import com.compomics.pladipus.repository.dao.impl.UserDAOImpl;
import com.compomics.pladipus.repository.dao.impl.UserRoleDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowGlobalParamDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowStepDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowStepParamDAOImpl;
import com.compomics.pladipus.repository.helpers.impl.BasicEncryptor;
import com.compomics.pladipus.repository.service.DefaultService;
import com.compomics.pladipus.repository.service.UserService;
import com.compomics.pladipus.repository.service.WorkflowService;
import com.compomics.pladipus.repository.service.impl.DefaultServiceImpl;
import com.compomics.pladipus.repository.service.impl.UserServiceImpl;
import com.compomics.pladipus.repository.service.impl.WorkflowServiceImpl;
import com.compomics.pladipus.shared.config.SharedConfiguration;

@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:application.properties", "classpath:hibernate.properties"})
@Import(SharedConfiguration.class)
public class RepositoryConfiguration {
	
	@Autowired
	private Environment env;
	
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_USER = "db.username";   
    private static final String DB_HOST = "db.host";
    private static final String DB_PORT = "db.port";
    private static final String DB_SCHEMA = "db.schema";
    private static final String DB_URL_START = "jdbc:mysql://";
	
    @Bean
	public BasicEncryptor basicEncryptor() {
		return new BasicEncryptor();
	}
    
	@Lazy
    @Bean
    public DataSource dataSource() {
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
             
        dataSource.setDriverClassName(DB_DRIVER);
        dataSource.setUrl(constructDbUrl());
        dataSource.setUsername(env.getRequiredProperty(DB_USER));       
        dataSource.setPassword(basicEncryptor().decryptString(env.getRequiredProperty(DB_PASSWORD)));
     
        return dataSource;
    }
	
	private String constructDbUrl() {
		return DB_URL_START +
			   env.getRequiredProperty(DB_HOST) + ":" +
			   env.getRequiredProperty(DB_PORT) + "/" +
			   env.getRequiredProperty(DB_SCHEMA) +
			   "?useSSL=false";
	}
	
	@Lazy
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	   LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
	   em.setDataSource(dataSource());
	   em.setPackagesToScan(new String[] { "com.compomics.pladipus.model.hibernate" });
	   JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	   em.setJpaVendorAdapter(vendorAdapter);
	   em.setJpaProperties(hibernateProperties());
	   return em;
	}
	
	@Lazy
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		  
		return transactionManager;
	}
	  
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	Properties hibernateProperties() {  
		return new Properties() {  

			private static final long serialVersionUID = 1L;

			{  
				setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));  
				setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));  
				setProperty("hibernate.globally_quoted_identifiers", env.getProperty("hibernate.globally_quoted_identifiers"));  
		    }  
		};  
	} 
	
	@Lazy
	@Bean
	public UserDAO hibuserDAO() {
		return new UserDAO();
	}
	
	@Lazy
	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}
	
	@Lazy
	@Bean
	public WorkflowService workflowService() {
		return new WorkflowServiceImpl();
	}
	
	@Lazy
	@Bean
	public DefaultService defaultService() {
		return new DefaultServiceImpl();
	}
	
	@Lazy
	@Bean
	public BaseDAO<User> userDAO() {
		return new UserDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<User> userRoleDAO() {
		return new UserRoleDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<Workflow> workflowDAO() {
		return new WorkflowDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<Step> workflowStepDAO() {
		return new WorkflowStepDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<Default> defaultDAO() {
		return new DefaultDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<Parameter> workflowGlobalParamDAO() {
		return new WorkflowGlobalParamDAOImpl(dataSource());
	}
	
	@Lazy
	@Bean
	public BaseDAO<Parameter> workflowStepParamDAO() {
		return new WorkflowStepParamDAOImpl(dataSource());
	}
}
