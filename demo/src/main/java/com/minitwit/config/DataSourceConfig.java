package com.minitwit.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@PropertySource("classpath:db.properties")
public class DataSourceConfig {
	
	@Autowired
	Environment env;
//	@Value("${username}")
//  String username;
	
	
	@Bean
	@Profile("test")
	public DataSource dataSource() {
		
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("sql/create-db.sql")
				.addScript("sql/insert-data.sql")
				.setScriptEncoding("UTF-8")
				.build();
		return db;
	}

	@Bean
	@Profile("dev")
	public DataSource dataSource2(){
		ComboPooledDataSource ds=new ComboPooledDataSource();
		ds.setPassword(env.getProperty("password"));
		ds.setUser(env.getProperty("username"));
		ds.setJdbcUrl(env.getProperty("url"));
		try {
			ds.setDriverClass(env.getProperty("driverClass"));
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ds;
	}
	
}
