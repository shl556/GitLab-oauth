package com.h3c.DFSDemo;

import java.beans.PropertyVetoException;

import org.csource.fastdfs.StorageClient1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.h3c.Util.FastDFSUtill;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@ComponentScan("com.h3c")
public class Config {
    
	@Bean
	public ComboPooledDataSource comboPooledDataSource(){
       ComboPooledDataSource source=new ComboPooledDataSource();
       source.setJdbcUrl("jdbc:mysql://172.27.12.62:3306/fastDFS?characterEncoding=utf8");
       try {
		source.setDriverClass("com.mysql.cj.jdbc.Driver");
	  } catch (PropertyVetoException e) {
		e.printStackTrace();
	 }
       source.setPassword("root");
       source.setUser("root");
       return source;
	}
	
	@Bean
	public NamedParameterJdbcTemplate jdbcTemplate(){
		NamedParameterJdbcTemplate template=new NamedParameterJdbcTemplate(comboPooledDataSource());
		return template;
	}
	
	@Bean
	public StorageClient1 storageClient1(){
		return FastDFSUtill.getStorageClient1();
	}
}
