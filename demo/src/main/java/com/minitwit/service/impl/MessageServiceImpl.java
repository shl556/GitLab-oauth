package com.minitwit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minitwit.dao.MessageDao;
import com.minitwit.dao.UserDao;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.model.Condition;
import com.minitwit.model.Error;
import com.minitwit.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService{

	@Autowired
	private MessageDao messageDao;
	

	@Override
	public User getUserTimelineMessages(int id,User user) {
        if (id==0||user==null) {
        	user.setError(new Error("待查询的用户id或者当前用户信息不能为空", "402"));
		}else{
        List<Message> messages=messageDao.getUserTimelineMessages(id);
        user.setMessages(messages);
		}
		return user;
	}

	@Override
	public User getUserFullTimelineMessages(int id ,User user) {
		 if (id==0||user==null) {
	        	user.setError(new Error("待查询的用户id或者当前用户信息不能为空", "402"));
			}else{
	        List<Message> messages=messageDao.getUserFullTimelineMessages(id);
	        user.setMessages(messages);
			}
			return user;
	}

	@Override
	public List<Message> getPublicTimelineMessages() {
		List<Message> messages=messageDao.getPublicTimelineMessages();
		return messages;
	}

	@Override
	public Error insertMessage(Message m) {
		if(m==null){
			return new Error("用户信息不能为空", "402");
		}
		messageDao.insertMessage(m);
		return null;
	}

	@Override
	public Error deleteMessage(int id) {
		if(id==0){
			return new Error("信息id不能为空", "402");
		}
		messageDao.deleteMessage(id);
		return null;
	}

	@Override
	public Error updateMessage(Message m) {
		if(m==null){
			return new Error("用户信息不能为空", "402");
		}
		messageDao.updateMessage(m);
		return null;
	}

	@Override
	public User getMessageByCondition(User user,Condition condition) {
		if (condition==null) {
			return new User(new Error("查询条件不能为空", "402"));
		}
		List<Message> messages=messageDao.getMessageByCondition(condition.getSql(), condition.getParams());
		user.setMessages(messages);
		return user;
	}

	@Override
	public Message getMessageById(int id) {
		if(id==0){
			return new Message(new Error("信息id不能为空", "402"));
		}
		Message message=messageDao.getMessageById(id);
		return message;
	}
	
	

}
