package com.minitwit.dao.impl;



import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.minitwit.config.AppConfig;
import com.minitwit.dao.MessageDao;
import com.minitwit.model.Condition;
import com.minitwit.model.Message;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=AppConfig.class)
//@ContextConfiguration("/appConfig.xml")
@ActiveProfiles("test")
@Transactional
@WebAppConfiguration//因为系统在扫描包的时候检测到有EableMvc注解所以必须加上@WebAppConfiguration注解，否则报java.lang.IllegalArgumentException: A ServletContext is required to configure default servlet handling
public class MessageDaoImplTest {
	@Autowired
	private MessageDao messageDao;

	
	@Test
	public void testGetUserTimelineMessages() {
		List<Message> messages=messageDao.getUserTimelineMessages(1);
		assertThat(messages, hasSize(28));
		List<Message> messages2=messageDao.getUserTimelineMessages(12);
		assertThat(messages2,empty());
	}

	@Test
	public void testGetUserFullTimelineMessages() {
		List<Message> messages=messageDao.getUserFullTimelineMessages(1);
		assertThat(messages, hasSize(140));
		List<Message> messages2=messageDao.getUserFullTimelineMessages(12);
		assertThat(messages2,empty());
	}

	@Test
	public void testGetPublicTimelineMessages() {
		   List<Message> messages=messageDao.getPublicTimelineMessages();
		   assertThat(messages, hasSize(213));
	}

	@Test(expected=DataIntegrityViolationException.class)
	public void testInsertMessage() {
		List<Message> before=messageDao.getUserTimelineMessages(2);
		Message message=new Message();
		message.setPubDate(new Date());
		message.setText("hello world");
		message.setUserId(2);
		message.setUsername("user002");
		messageDao.insertMessage(message);
		List<Message> after=messageDao.getUserTimelineMessages(2);
		int result=after.size()-before.size();
		assertThat(result, equalTo(1));
		
		
		Message message2=new Message();
		messageDao.insertMessage(message2);
		
		Message message3=null;
		messageDao.insertMessage(message3);
		
		message.setUserId(3);
		messageDao.insertMessage(message);
	}

	@Test
	public void testDeleteMessage() {
		  List<Message> messages=messageDao.getUserFullTimelineMessages(2);
		  Message message=messages.get(0);
		  messageDao.deleteMessage(message.getId());
		  
		  message=messages.get(1);
		  messageDao.deleteMessage(message.getId());
		  
		  message=messages.get(2);
		  messageDao.deleteMessage(message.getId());
		  
		  List<Message> messages2=messageDao.getUserFullTimelineMessages(2);
		  
		  int delete=messages.size()-messages2.size();
		  
		  assertThat(delete, equalTo(3));
		  
		  messageDao.deleteMessage(10);
	}

	@Test
	public void testUpdateMessage() {
		  List<Message> messages=messageDao.getUserFullTimelineMessages(2);
		  Message message=messages.get(0);
		  message.setPubDate(new Date());
		  message.setText(" my world");
          messageDao.updateMessage(message);
          
          messages=messageDao.getUserFullTimelineMessages(2);
          Message message2=messages.get(0);
          
          assertThat(message2.getText(), equalToIgnoringWhiteSpace("my world"));
	}

	@Test
	public void testGetMessageByCondition() {
		  Condition condition=new Condition();
		  condition.setKeyWord("一周");
		  condition.setPubDate("4");
		  condition.setUserId(2);
		  List<Message> messages=messageDao.getMessageByCondition(condition.getSql(), condition.getParams());
		  assertThat(messages, hasSize(6));
	}

	@Test
	public void testGetMessageById() {
		  List<Message> messages=messageDao.getUserFullTimelineMessages(2);
		  Message message=messages.get(2);
		  Message message2=messageDao.getMessageById(message.getId());
		  assertEquals(message.getText(), message2.getText());
		  
		  Message message3=messageDao.getMessageById(10);
		  assertThat(message3.getText(), isEmptyOrNullString());
	}

}
