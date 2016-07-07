package com.shl.mongodb;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseDaoImplTest {
	private static BaseDaoImpl<User> mongodb;
	private static PropertyUtilsBean beanutil;
	@BeforeClass
	public static void init(){
		 mongodb=new BaseDaoImpl<>();
	     //不能使用PropertyUtilsBean将bean转化为Map，这样做会丢失bean属性的java类型
//		 beanutil=new PropertyUtilsBean();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	    BaseDaoImpl.close();
	}


	@Test
	public void testDelete() {
		boolean result =mongodb.delete("shl", "test", eq("username", "shl0"));
		assertTrue(result);
		result =mongodb.delete("shl", "test", eq("username", "shl0"));
		assertFalse(result);
	}

	@Test
	public void testFind() {
		List<User> users=mongodb.find("shl", "test", and(gte("age", 24),lte("age", 28)), User.class);
	    assertThat(users, hasSize(483));
	    
	    //注意正则表达式的格式，在命令行中必须使用js格式的Regex,在java客户端中使用java格式的Regex即可
	    users=mongodb.find("shl", "test", regex("username", "shl99?"), User.class);
	    System.out.println(users.size());
	}

	@Test
	public void testAdd() throws Exception {
		User user=new User();
		Date date=DateUtils.parseDate("1980-12-02", "yyyy-MM-dd");
		for (int i = 0; i < 1000; i++) {
			user.setAge(RandomUtils.nextInt(20, 30));
			user.setScore(RandomUtils.nextInt(40,150));
			user.setAddress("北京"+i);
			user.setBirthday(DateUtils.addMonths(date, RandomUtils.nextInt(0, 24)));
		    user.setPassword(RandomStringUtils.random(6, "abcdefghijk"));
		    user.setUsername("shl"+i);
		    mongodb.add("shl", "test",user.getMap());
		}
	}

	@Test
	public void testIsExit() {
		boolean exit=mongodb.isExit("shl", "test", eq("score",148));
	    assertTrue(exit);
	}

	@Test
	public void testUpdate() {
		boolean result=mongodb.update("shl", "test", eq("username", "shl0"),combine(set("score", 130),set("age", "34")));
	    assertTrue(result);
	    result=mongodb.update("shl", "test", eq("username", "shl1001"),combine(set("score", 120),set("age", "32")));
	    assertFalse(result);
	}
     
	@Test
	public void testGetByCondition(){
		PageInfo page=new PageInfo(483, 15);
		page.setCurrentPageNum(1);
		List<User> users=mongodb.getByCondition("shl", "test",User.class, and(gte("age", 24),lte("age", 28)), page);
		assertThat(users,hasSize(15));
		users.forEach(user->System.out.println(user.getMap()));
	}
	
}
