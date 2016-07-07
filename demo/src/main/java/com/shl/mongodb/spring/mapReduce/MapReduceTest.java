package com.shl.mongodb.spring.mapReduce;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.shl.mongodb.spring.AppConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class MapReduceTest {

	@Autowired
	MongoTemplate template;
	
	@Test
	public void insertData(){
		Random random=new Random();
		for(int i=0;i<1000;i++){
			int a=random.nextInt(10);
			//三个用户在100个产品中随机选择，每次插入操作选择一个产品，产品的单价在200以内随机。
			Consumer user=new Consumer();
			if(a<=3){
			    user.setUsername("Job");
			}else if(a<=6){
			    user.setUsername("Mary");
			}else{
				user.setUsername("shl");
			}
			user.setGoodsId(random.nextInt(100));
			user.setPrice(random.nextInt(200));
			template.insert(user);
		}
	}
	
	@Test
	public void  mapReduceTest(){
		//计算每个用户购买了多少个产品
		MapReduceResults<ValueObject> results=template.mapReduce("consumer", "classpath:map.js", "classpath:reduce.js", ValueObject.class);
	    results.forEach(s -> System.out.println(s));
	    
	}
	@Test
	public void  mapReduceTest2(){
		//计算每个用户购买的每种产品的数量各是多少
		MapReduceResults<ValueObject> results=template.mapReduce("consumer", "classpath:map2.js", "classpath:reduce.js", ValueObject.class);
		results.forEach(s -> System.out.println(s));
		
	}
	@Test
	public void  mapReduceTest3(){
		//计算每个用户购买的总的商品数量和总的单价是多少
		//当返回的数据是一个对象的形式时就无法计算，不知道是为什么，命令行同样如此。
		MapReduceResults<ValueObject2> results=template.mapReduce("consumer", "classpath:map3.js", "classpath:reduce3.js", ValueObject2.class);
		results.forEach(s -> System.out.println(s));
		
	}
	
	@Test
	public void scriptTest(){
		ScriptOperations scriptOps = template.scriptOps();
        
		//直接执行javascript脚本
		ExecutableMongoScript echoScript = new ExecutableMongoScript("function(x) { return x; }");
		scriptOps.execute(echoScript, "directly execute script");     
        
		//注册命名脚本
		scriptOps.register(new NamedMongoScript("echo", echoScript)); 
		//调用指定名称的脚本
		scriptOps.call("echo", "execute script via name");   
	}
}
