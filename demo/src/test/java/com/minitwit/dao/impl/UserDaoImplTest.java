package com.minitwit.dao.impl;


import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.minitwit.config.AppConfig;
import com.minitwit.dao.UserDao;
import com.minitwit.model.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= AppConfig.class)
@ActiveProfiles("test")
@Transactional
@WebAppConfiguration
public class UserDaoImplTest {
	@Autowired
	private UserDao userDao;
	@Test
	public void testGetUserbyUsername() {
		   User user=userDao.getUserbyUsername("user002");
		   User user2=userDao.getUserbyUsername("user");
		   assertThat(user.getUsername(),equalToIgnoringCase("user002"));
		   assertNull(user2);
	}

	@Test
	public void testInsertFollower() {
		   User user=userDao.getUserbyUsername("user002");
		   User user2=userDao.getUserbyUsername("user003");
		   userDao.insertFollower(user, user2);
		   boolean followee=userDao.isUserFollower(user, user2);
		   assertTrue(followee);
	}

	@Test
	public void testDeleteFollower() {
		  User user=userDao.getUserbyUsername("user002");
		   User user2=userDao.getUserbyUsername("user003");
		   userDao.insertFollower(user, user2);
		   userDao.deleteFollower(user,user2);
		   boolean followee=userDao.isUserFollower(user, user2);
		   assertFalse(followee);
	}

	@Test
	public void testIsUserFollower() {
		User user=userDao.getUserbyUsername("user002");
		   User user2=userDao.getUserbyUsername("user003");
		   userDao.insertFollower(user, user2);
		   userDao.isUserFollower(user, user2);
	}

	@Test
	public void testRegisterUser() {
		  User user=new User();
		  user.setEmail("1021270919@qq.com");
		  user.setPassword("123456");
		  user.setPassword2("12345");
		  user.setUsername("shl");
		  userDao.registerUser(user);
		  User user2=userDao.getUserbyUsername("shl");
		  assertEquals("123456", user2.getPassword());
	}

}
