package com.shl.sparkRest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserRestClientTest {

	@Autowired
	private UserRestClient client;

	@Test
	public void testGetUsers() {
		assertEquals(client.getUsers(), 4);
	}

	@Test
	public void testGetUserById() {
		User user = client.getUserById(2);
		assertEquals(user.getUserName(), "shl2");
		User user2= client.getUserById(6);
		assertEquals(user2.getError().getErrorCode(), "401");
	}

	@Test
	public void testUpdateUsers() {
		User user=new User(3, "shl5", "北京", 26, "123456");
		User result=client.updateUsers(user);
		assertEquals(user.getUserName(), result.getUserName());
		User user2=new User(5, "shl6", "北京", 26, "123456");
		User result2=client.updateUsers(user2);
        assertEquals("401", result2.getError().getErrorCode());		
	}

	@Test
	public void testDeleteUsers() {
		int result=client.deleteUsers(2);
		assertEquals(1, result);
		int result2=client.deleteUsers(7);
		assertEquals(-1, result2);
	}

	@Test
	public void testCreateUser(){
		User user=new User();
		user.setAddress("北京");
		user.setUserName("shl");
		user.setAge(26);
		user.setPassword("123456");
		
		User user2=new User();
		user2.setAddress("北京");
		user2.setUserName("shl5");
		user2.setAge(26);
		user2.setPassword("123456");
		
		User result=client.createUser(user2);
		assertEquals(5, result.getId());
		User result2=client.createUser(user);
		assertEquals("402", result2.getError().getErrorCode());
	}
}
