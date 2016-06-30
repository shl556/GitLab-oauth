package com.minitwit.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minitwit.dao.UserDao;
import com.minitwit.model.Error;
import com.minitwit.model.User;
import com.minitwit.service.UserService;
import com.minitwit.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Override
	public User getUserbyUsername(String username) {
        if(!StringUtils.isNotEmpty(username)){
        	return new User(new Error("username不能为空","401"));
        }
        User user=userDao.getUserbyUsername(username);
		return user;
	}

	@Override
	public Error  insertFollower(User follower, User followee) {
		if(followee==null||follower==null){
			return new Error("用户信息为空", "402");
		}
		userDao.insertFollower(follower, followee);
		return null;
	}

	@Override
	public Error deleteFollower(User follower, User followee) {
		if(followee==null||follower==null){
			return new Error("用户信息为空", "402");
		}
		userDao.deleteFollower(follower, followee);
		return null;
	}

	@Override
	public Error isUserFollower(User follower, User followee) {
		if(followee==null||follower==null){
			return new Error("用户信息为空", "402");
		}
		boolean result=userDao.isUserFollower(follower, followee);
		if (result) {
			return new Error("匹配成功", "200");
		}else{
			return new Error("不匹配", "404");
		}
	}

	@Override
	public Error registerUser(User user) {
		if(user==null){
			return new Error("用户信息为空", "402");
		}
		userDao.registerUser(user);
		return null;
	}

	@Override
	public User autoLogin(String cookieString) {
		if(!StringUtils.isNotEmpty(cookieString)){
		    return new User(new Error("cookiez字符串为空", "402"));
		}
		String[] loginInfo=cookieString.split("-");
		String userName=loginInfo[0];
		String password=loginInfo[1];
		User userFound = userDao.getUserbyUsername(userName);
		if(userFound == null||!password.equals(userFound.getPassword())) {
		     userFound=new User(new Error("用户名或者密码不正确", "402"));
		     System.err.println("cookie中用户名或者密码不匹配");
		     System.out.println(userName+" :"+password+" :"+userFound.getPassword());
		}
		return userFound;
	}

	@Override
	public User checkUser(User user) {
		if(user==null){
			return new User(new Error("用户信息不能为空", "401"));
		}
		User userFound = userDao.getUserbyUsername(user.getUsername());
		if(userFound == null) {
			userFound=new User(new Error("用户名不存在", "402"));
		} else if(!PasswordUtil.verifyPassword(user.getPassword(), userFound.getPassword())) {
			userFound=new User(new Error("密码不匹配", "403"));
		} 
		return userFound;
	}

}
