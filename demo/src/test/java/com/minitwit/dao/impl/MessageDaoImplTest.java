package com.minitwit.dao.impl;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.minitwit.App;
import com.minitwit.dao.MessageDao;
import com.minitwit.dao.UserDao;
import com.minitwit.model.Condition;
import com.minitwit.model.Message;
import com.minitwit.model.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=App.class)
public class MessageDaoImplTest {

	@Autowired
	private MessageDao messageDao;
	@Autowired
	private UserDao userDao;
	@Test
	public void testGetUserTimelineMessages() {
		   User user =userDao.getUserbyUsername("user002");
		   List<Message> messages=messageDao.getUserTimelineMessages(user);
           assertNotNull(messages);
	}

	@Test
	public void testGetUserFullTimelineMessages() {
		   User user =userDao.getUserbyUsername("user002");
		   List<Message> messages=messageDao.getUserFullTimelineMessages(user);
           assertNotNull(messages);
	}

	@Test
	public void testGetPublicTimelineMessages() {
		   List<Message> messages=messageDao.getPublicTimelineMessages();
		   assertNotNull(messages);
	}

	@Test
	public void testInsertMessage() {
		Message message=new Message();
		User user=userDao.getUserbyUsername("user002");
		message.setPubDate(new Date());
		message.setText("hello world");
		message.setUserId(user.getId());
		message.setUsername(user.getUsername());
		messageDao.insertMessage(message);
	}

	@Test
	public void testDeleteMessage() {
		User user=userDao.getUserbyUsername("user002");
		  List<Message> messages=messageDao.getUserFullTimelineMessages(user);
		  Message message=messages.get(0);
		  messageDao.deleteMessage(message.getId());
	}

	@Test
	public void testUpdateMessage() {
		User user=userDao.getUserbyUsername("user002");
		  List<Message> messages=messageDao.getUserFullTimelineMessages(user);
		  Message message=messages.get(0);
		  message.setPubDate(new Date());
		  message.setText(" my world");
          messageDao.updateMessage(message);
	}

	@Test
	public void testGetMessageByCondition() {
		  Condition condition=new Condition();
		  condition.setKeyWord("一周");
		  condition.setPubDate("4");
		  User user=userDao.getUserbyUsername("user002");
		  condition.setUserId(user.getId());
		  List<Message> messages=messageDao.getMessageByCondition(condition.getSql(), condition.getParams());
		  assertNotNull(messages);
	}

	@Test
	public void testGetMessageById() {
		User user=userDao.getUserbyUsername("user002");
		  List<Message> messages=messageDao.getUserFullTimelineMessages(user);
		  Message message=messages.get(2);
		  Message message2=messageDao.getMessageById(message.getId());
		  assertEquals(message.getText(), message2.getText());
	}

}
