package com.compomics.pladipus.repository.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.compomics.pladipus.model.config.ModelConfiguration;
import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.impl.DefaultDAOImpl;
import com.compomics.pladipus.repository.dao.impl.UserDAOImpl;
import com.compomics.pladipus.repository.dao.impl.UserRoleDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowGlobalParamDAOImpl;
import com.compomics.pladipus.repository.dao.impl.WorkflowStepDAOImpl;
import com.compomics.pladipus.repository.helpers.impl.BasicEncryptor;
import com.compomics.pladipus.repository.service.DefaultService;
import com.compomics.pladipus.repository.service.UserService;
import com.compomics.pladipus.repository.service.WorkflowService;
import com.compomics.pladipus.repository.service.impl.DefaultServiceImpl;
import com.compomics.pladipus.repository.service.impl.UserServiceImpl;
import com.compomics.pladipus.repository.service.impl.WorkflowServiceImpl;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
@Import(ModelConfiguration.class)
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
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
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
}
