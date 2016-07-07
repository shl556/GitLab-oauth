package com.shl.mongodb.spring;

import static org.hamcrest.Matchers.sameInstance;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.shl.mongodb.User;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class MongodbTest2 {

	@Autowired
	private MongoTemplate mongoOps;

//	@Autowired
//	private UserRepository repository;
	
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void exampleTest(){
//		User user=new User();
//		user.setUsername("shl0");
//		ExampleMatcher matcher=ExampleMatcher.matching().withMatcher("username", GenericPropertyMatchers.endsWith());
//	    
//		Iterator<User> users=repository.findAll(Example.of(user, matcher)).iterator();
//	    while(users.hasNext()){
//	    	User user2=users.next();
//	    	System.out.println(user2);
//	    }
	}
	
	
}
