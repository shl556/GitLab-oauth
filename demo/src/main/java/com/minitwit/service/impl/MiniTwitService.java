package com.minitwit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minitwit.dao.MessageDao;
import com.minitwit.dao.UserDao;
import com.minitwit.model.Condition;
import com.minitwit.model.LoginResult;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.util.PasswordUtil;

@Service
public class MiniTwitService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MessageDao messageDao;
	
	public List<Message> getUserFullTimelineMessages(User user) {
		return messageDao.getUserFullTimelineMessages(user);
	}
	
	public List<Message> getUserTimelineMessages(User user) {
		return messageDao.getUserTimelineMessages(user);
	}
	
	public List<Message> getPublicTimelineMessages() {
		return messageDao.getPublicTimelineMessages();
	}
	
	public User getUserbyUsername(String username) {
		return userDao.getUserbyUsername(username);
	}
	
	public void followUser(User follower, User followee) {
		userDao.insertFollower(follower, followee);
	}
	
	public void unfollowUser(User follower, User followee) {
		userDao.deleteFollower(follower, followee);
	}
	
	public boolean isUserFollower(User follower, User followee) {
		return userDao.isUserFollower(follower, followee);
	}
	
	public void addMessage(Message message) {
		messageDao.insertMessage(message);
	}
	
	/**根据用户名查找用户，若用户存在则比较密码是否相同，不同则登陆失败，返回对应提示信息
	 * 若相同则返回经过LoginResult包装的user对象
	 * @param user
	 * @return
	 */
	public LoginResult checkUser(User user) {
		LoginResult result = new LoginResult();
		User userFound = userDao.getUserbyUsername(user.getUsername());
		if(userFound == null) {
			result.setError("Invalid username");
		} else if(!PasswordUtil.verifyPassword(user.getPassword(), userFound.getPassword())) {
			result.setError("Invalid password");
		} else {
			result.setUser(userFound);
		}
		
		return result;
	}
	
	/**  检查cookie中的用户名与密码是否匹配
	 * @param userName
	 * @param password
	 * @return
	 */
	public  LoginResult  autoLogin(String cookieString){
		LoginResult result = new LoginResult();
		if(cookieString!=null){
		String[] loginInfo=cookieString.split("-");
		String userName=loginInfo[0];
		String password=loginInfo[1];
		User userFound = userDao.getUserbyUsername(userName);
		if(userFound == null||!password.equals(userFound.getPassword())) {
//		     result.setError("用户名或者密码错误");
		     System.err.println("cookie中用户名或者密码不匹配");
		     System.out.println(userName+" :"+password+" :"+userFound.getPassword());
		}else{
			result.setUser(userFound);
		}
		}
		return result;
		
	}
	public void registerUser(User user) {
		user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
		userDao.registerUser(user);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	public void deleteMessage(int id){
		 messageDao.deleteMessage(id);
	}
	
	public void updateMessage(Message message){
		  messageDao.updateMessage(message);
	}
	
	public List<Message> getMessageByCondition(Condition condition){
		   List<Message> messages=messageDao.getMessageByCondition(condition.getSql(), condition.getParams());
		   return messages;
	}
	
	public Message getMessageById(int id){
		 Message message=messageDao.getMessageById(id);
		 return message;
	}
}
