package com.minitwit.config;



import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import(DataSourceConfig.class)
@ComponentScan(basePackages="com.minitwit",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class})})
public class AppConfig {

	@Autowired
	DataSource dataSource;
	
	@Bean
	public PlatformTransactionManager getTransactionManager(){
		PlatformTransactionManager manager=new DataSourceTransactionManager(dataSource);
		return manager;
	}
	
}
