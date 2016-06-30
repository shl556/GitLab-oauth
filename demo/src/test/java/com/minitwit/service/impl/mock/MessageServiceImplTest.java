package com.minitwit.service.impl.mock;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.minitwit.dao.impl.MessageDaoImpl;
import com.minitwit.model.Condition;
import com.minitwit.model.Error;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.service.impl.MessageServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {
    @Mock
    MessageDaoImpl messageDao;
    @InjectMocks
    MessageServiceImpl messageService;
	
	@Test
	public void testGetUserTimelineMessages() {
		User user=messageService.getUserTimelineMessages(0, new User());
	    assertThat(user.getError().getErrorCode(),equalToIgnoringWhiteSpace("402"));
	    
	    List<Message> messages=new ArrayList<>();
	    messages.add(new Message());
	    when(messageDao.getUserTimelineMessages(2)).thenReturn(messages);
	    
	    user=messageService.getUserTimelineMessages(2, new User());
	    assertThat(user.getMessages(),is(messages));
	    assertNull(user.getError());
	    
	}

	@Test
	public void testGetUserFullTimelineMessages() {
		User user=messageService.getUserTimelineMessages(0, new User());
	    assertThat(user.getError().getErrorCode(),equalToIgnoringWhiteSpace("402"));
	    
	    List<Message> messages=new ArrayList<>();
	    messages.add(new Message());
	    when(messageDao.getUserFullTimelineMessages(2)).thenReturn(messages);
	    
	    user=messageService.getUserFullTimelineMessages(2, new User());
	    assertThat(user.getMessages(),is(messages));
	    assertNull(user.getError());
	    
	}

	@Test
	public void testGetPublicTimelineMessages() {
		List<Message> messages=new ArrayList<>();
		when(messageDao.getPublicTimelineMessages()).thenReturn(messages);
		
		List<Message> result=messageService.getPublicTimelineMessages();
		
		assertThat(result, is(messages));
		
	}

	@Test
	public void testInsertMessage() {
		Error error=messageService.insertMessage(null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		Message m=new Message();
		assertNull(messageService.insertMessage(m));
	}

	@Test
	public void testDeleteMessage() {
		Error error=messageService.insertMessage(null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		assertNull(messageService.deleteMessage(12));
	}

	@Test
	public void testUpdateMessage() {
		Error error=messageService.insertMessage(null);
		assertThat(error.getErrorCode(), equalTo("402"));
		
		Message m=new Message();
		assertNull(messageService.updateMessage(m));
	}

	@Test
	public void testGetMessageByCondition() {
		User user=messageService.getMessageByCondition(new User(), null);
		assertThat(user.getError().getErrorCode(), equalTo("402"));
		
		List<Message> messages=new ArrayList<>();
	    Condition condition=new Condition();
		when(messageDao.getMessageByCondition(condition.getSql(), condition.getParams())).thenReturn(messages);
		
		user=messageService.getMessageByCondition(new User(), condition);
	    assertThat(user.getMessages(), is(messages));	
	
		
	}

	@Test
	public void testGetMessageById() {
		Message result=messageService.getMessageById(0);
		assertThat(result.getError().getErrorCode(), equalTo("402"));
		
		Message m=new Message();
		when(messageDao.getMessageById(2)).thenReturn(m);
		
		result=messageService.getMessageById(2);
		assertThat(result, is(m));
		
	}

}
