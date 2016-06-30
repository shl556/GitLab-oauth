package com.minitwit.service.impl.mock;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.minitwit.dao.impl.UserDaoImpl;
import com.minitwit.model.User;
import com.minitwit.service.impl.UserServiceImpl;
import com.minitwit.util.PasswordUtil;
import com.minitwit.model.Error;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	@Mock
	UserDaoImpl userDao;
	@InjectMocks
	UserServiceImpl userService;
	
	@Test
	public void testGetUserbyUsername() {
		User result=userService.getUserbyUsername("");
		assertThat(result.getError().getErrorCode(),equalTo("401"));
		
		User user2=new User();
		when(userDao.getUserbyUsername("user002")).thenReturn(user2);
		
		result=userService.getUserbyUsername("user002");
		assertThat(result, is(user2));
		
	}

	@Test
	public void testInsertFollower() {
		Error error=userService.insertFollower(new User(),null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		error=userService.insertFollower(new User(), new User());
		assertNull(error);
	}

	@Test
	public void testDeleteFollower() {
		Error error=userService.deleteFollower(new User(),null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		error=userService.insertFollower(new User(), new User());
		assertNull(error);
	}

	@Test
	public void testIsUserFollower() {
		Error error=userService.isUserFollower(new User(),null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		error=userService.insertFollower(new User(), new User());
		assertNull(error);
	}

	@Test
	public void testRegisterUser() {
		Error error=userService.registerUser(null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		error=userService.registerUser(new User());
		assertNull(error);
	}

	@Test
	public void testAutoLogin() {
		User result=userService.autoLogin("");
	    assertThat(result.getError().getErrorCode(), equalTo("402"));
	    
	    User user2=new User();
	    user2.setPassword("123456");
	    String cookString="user002-123456";
	    when(userDao.getUserbyUsername("user002")).thenReturn(user2);
	    result=userService.autoLogin(cookString);
	    assertThat(result,is(user2));
	    
	    user2.setPassword("23456");
	    when(userDao.getUserbyUsername("user002")).thenReturn(user2);
	    result=userService.autoLogin(cookString);
	    assertThat(result.getError().getErrorCode(), equalTo("402"));
	}

	@Test
	public void testCheckUser() {
		User result=userService.checkUser(null);
		assertThat(result.getError().getErrorCode(), equalTo("401"));
		
		User user=new User();
		user.setUsername("user002");
		user.setPassword("123456");
		User user2=new User();
		user2.setUsername("user002");
		user2.setPassword(PasswordUtil.hashPassword("123456"));
		
		when(userDao.getUserbyUsername(user.getUsername())).thenReturn(user2);
		result=userService.checkUser(user);
		assertThat(result, is(user2));
		
		user2.setPassword(PasswordUtil.hashPassword("1234567"));
		when(userDao.getUserbyUsername(user.getUsername())).thenReturn(user2);
		result=userService.checkUser(user);
		assertThat(result.getError().getErrorCode(),equalTo("403"));
		
		when(userDao.getUserbyUsername(user.getUsername())).thenReturn(null);
		result=userService.checkUser(user);
		assertThat(result.getError().getErrorCode(), equalTo("402"));
	}

}
