package com.compomics.pladipus.repository.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Override data source for test purposes.  Creates an in-memory database for unit tests.
 */
@Configuration
public class TestRepositoryConfiguration {
	@Bean
	public DataSource dataSource() {

		EmbeddedDatabase source = getEmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.build();
		return source;
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
                        dataSource.setUrl("jdbc:h2:mem:testdb;DATABASE_TO_UPPER=false;MODE=mysql;"
                        		+ "INIT=RUNSCRIPT FROM 'classpath:initDb.sql'\\;RUNSCRIPT FROM 'classpath:insertTestUsers.sql'\\" 
                        		+ ";RUNSCRIPT FROM 'classpath:insertTestWorkflows.sql'\\;SET SCHEMA pladipus2");
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
}
