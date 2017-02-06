package com.compomics.pladipus.repository.config;

import java.sql.Driver;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Override data source for test purposes.  Creates an in-memory database for unit tests.
 */
@Configuration
public class TestRepositoryConfiguration {
	
	@Bean
	public DataSource dataSource() {
		return getEmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
	}
	
	private EmbeddedDatabaseBuilder getEmbeddedDatabaseBuilder() {
        return new EmbeddedDatabaseBuilder().setDataSourceFactory(new DataSourceFactory() {
            private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            @Override
            public ConnectionProperties getConnectionProperties() {
                return new ConnectionProperties() {
                    @Override
                    public void setDriverClass(Class<? extends Driver> driverClass) {
                        dataSource.setDriverClass(org.h2.Driver.class);
                    }

                    @Override
                    public void setUrl(String url) {
                        dataSource.setUrl(getUrlString());
                    }
                    
                    private String getUrlString() {
                    	return "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
                    }
                    
					@Override
					public void setPassword(String arg0) {
						dataSource.setPassword("");
					}

					@Override
					public void setUsername(String arg0) {
						dataSource.setUsername("sa");
					}
                };
            }

            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        });
    }

	@Bean
	Properties hibernateProperties() {  
		return new Properties() {  
			
			static final long serialVersionUID = 4456554488993955458L;

			{  
				setProperty("hibernate.hbm2ddl.auto", "create");
				setProperty("hibernate.hbm2ddl.import_files", "insertTestUsers.sql");
				setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");  
				setProperty("hibernate.globally_quoted_identifiers", "true");
		    }  
		};  
	} 
}
