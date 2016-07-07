package com.shl.mongodb.spring;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

@Configuration
//启用spring jpa data支持，即声明CURD的接口并继承自spring data Respository接口后由spring data提供实现
//xml方式：<jpa:repositories base-package="com.acme.repositories"/>
//@EnableJpaRepositories(basePackages="com.shl.mongodb.spring")
public class AppConfig {

	//有两种实例化MongoClient的方式，推荐使用第二种，不抛出异常，可以将异常转换为Spring Data体系的异常
   public @Bean MongoClient mongo() throws UnknownHostException {
       return new  MongoClient("172.27.12.85");
   }
	
//	public @Bean MongoClientFactoryBean mongo() {
//        //构建Mongo实例不能构建MongoClient实例
//		MongoClientFactoryBean mongo = new MongoClientFactoryBean();
//        mongo.setHost("172.27.12.85");
//        return mongo;
//   }
	
	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
	    //最新版本的SimpleMongoDbFactory必须使用MongoClient实例化
		return new SimpleMongoDbFactory(mongo(), "shl");
	  }

	  public @Bean MongoTemplate mongoTemplate() throws Exception {
          //MongoTemplate实现了MongoOperations接口，该接口定义了常规的mongo操作
		  //MongoTemplate的默认转换器是MongoMappingConverter，可以通过构造方法指定自定义的转换器
		  //MongoTemplate同其他Template一样是线程安全的，可以被多个线程使用
//		  new MongoTemplate(mongo().getObject(), "shl");
		  return new MongoTemplate(mongoDbFactory());
	  }
}