package com.minitwit.controller.mock;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.minitwit.controller.MessageController;
import com.minitwit.controller.MvcResultHandle;
import com.minitwit.model.Condition;
import com.minitwit.model.Error;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.service.impl.MessageServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class MessageControllerTest{
    
	@Mock
	private MessageServiceImpl service;
    @InjectMocks
	private MessageController controller;
	
    private	MockMvc mvc;
	
    @Before
    public void setup(){
    	this.mvc=MockMvcBuilders.standaloneSetup(controller).build();
    }
    
   
	@Test
	public void testGetUserTimelineMessages() throws Exception {
		int id=1;
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
		User user2=new User();
		user2.setId(2);
		user2.setUsername("user002");
		
		List<Message> messages=new ArrayList<>();
		messages.add(new Message(2,"测试"));
		messages.add(new Message(2,"测试2"));
		messages.add(new Message(2,"测试3"));
		messages.add(new Message(2,"测试4"));
		messages.add(new Message(2,"测试5"));
		
		user2.setMessages(messages);
		
		when(service.getUserTimelineMessages(id, user)).thenReturn(user2);
		
        MvcResult result=mvc.perform(get("/messages/{id}/pub", id)
        		                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        		                    .sessionAttr("user", user))//发起请求
                            .andReturn();//结果处理
        User user3=new MvcResultHandle(result).getResult(User.class);
        assertThat(user3.getMessages(), hasSize(5));
        assertThat(user3.getUsername(), equalTo("user002"));
        
        when(service.getUserTimelineMessages(12, user)).thenReturn(user);
        MvcResult result2=mvc.perform(get("/messages/12/pub")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .sessionAttr("user", user))//发起请求
                .andReturn();//结果处理
      User user4=new MvcResultHandle(result2).getResult(User.class);
      assertThat(user4.getMessages(), nullValue());
      assertThat(user4.getUsername(), equalTo("user002"));
      
      verify(service).getUserTimelineMessages(1, user);
      verify(service).getUserTimelineMessages(12, user);
	}

	@Test
	public void testGetUserFullTimelineMessages() throws Exception {
		int id=1;
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
		User user2=new User();
		user2.setId(2);
		user2.setUsername("user002");
		
		List<Message> messages=new ArrayList<>();
		messages.add(new Message(2,"测试"));
		messages.add(new Message(2,"测试2"));
		messages.add(new Message(2,"测试3"));
		messages.add(new Message(2,"测试4"));
		messages.add(new Message(2,"测试5"));
		
		user2.setMessages(messages);
		
		when(service.getUserFullTimelineMessages(id, user)).thenReturn(user2);
        MvcResult result=mvc.perform(get("/messages/{id}/reply", id)
        		                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        		                    .sessionAttr("user", user))//发起请求
                            .andReturn();//结果处理
        User user3=new MvcResultHandle(result).getResult(User.class);
        assertThat(user3.getMessages(), hasSize(5));
        assertThat(user3.getUsername(), equalTo("user002"));
        
        when(service.getUserFullTimelineMessages(12, user)).thenReturn(user);
        MvcResult result2=mvc.perform(get("/messages/12/reply")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .sessionAttr("user", user))//发起请求
        .andReturn();//结果处理
      User user4=new MvcResultHandle(result2).getResult(User.class);
      assertThat(user4.getMessages(), nullValue());
      assertThat(user4.getUsername(), equalTo("user002"));
	
      verify(service).getUserFullTimelineMessages(1, user);
      verify(service).getUserFullTimelineMessages(12, user);
	}

	@Test
	public void testGetPublicTimelineMessages() throws Exception {
		List<Message> messages=new ArrayList<>();
		messages.add(new Message(2,"测试"));
		messages.add(new Message(2,"测试2"));
		messages.add(new Message(2,"测试3"));
		messages.add(new Message(2,"测试4"));
		messages.add(new Message(2,"测试5"));
		
		when(service.getPublicTimelineMessages()).thenReturn(messages);
		MvcResult result=mvc.perform(get("/messages").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				         .andReturn();
		
		messages=new MvcResultHandle(result).getResult(messages.getClass());
		assertThat(messages, hasSize(5));
	}

	@Test
	public void testInsertMessage() throws Exception {
	    
		//采用类似表单参数的方式模式
	    MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
	    params.add("text", "测试");
	    params.add("userId", "1");
	    
	    Message message=new Message(1, "测试");
	    when(service.insertMessage(message)).thenReturn(null);
	    
	    mvc.perform(put("/messages")
	    		         .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
	    		         .params(params))
	    		         .andExpect(content().string(""));
	    
	    verify(service).insertMessage(message);
	    
	}

	@Test
	public void testDeleteMessage() throws Exception {
		when(service.deleteMessage(112)).thenReturn(null);
		mvc.perform(delete("/messages/112")
				                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				               .andExpect(content().string(""));
	   
		when(service.deleteMessage(0)).thenReturn(new Error("id不能为0", "402"));
		MvcResult result=mvc.perform(delete("/messages/0")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                  .andReturn();
        Error error=new MvcResultHandle(result).getResult(Error.class);
        assertThat(error.getErrorCode(), equalTo("402"));
	    
	}

	@Test
	public void testUpdateMessage() throws Exception {
		Message message=new Message(112,"测试");
		when(service.updateMessage(message)).thenReturn(null);
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("userId", "112");
        params.add("text", "测试");
        
       mvc.perform(post("/messages")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                 .params(params))
              .andExpect(content().string(""));
       verify(service).updateMessage(message);
         
	}

	@Test
	public void testGetMessageByCondition() throws Exception {
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
		User user2=new User();
		user2.setId(2);
		user2.setUsername("user002");
		
		List<Message> messages=new ArrayList<>();
		messages.add(new Message(2,"测试"));
		messages.add(new Message(2,"测试2"));
		messages.add(new Message(2,"测试3"));
		messages.add(new Message(2,"测试4"));
		messages.add(new Message(2,"测试5"));
		
		user2.setMessages(messages);
		
		Condition condition=new Condition();
		condition.setKeyWord("一周");
		condition.setUserId(2);
		condition.setPubDate("4");
		
		when(service.getMessageByCondition(user, condition)).thenReturn(user2);
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("keyWord", "一周");
		params.add("pubDate", "4");
		params.add("userId", "2");
		
		MvcResult result=mvc.perform(post("/messages/condition")
				        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
				        .params(params)
				        .sessionAttr("user", user))
				        .andReturn();
		
		User userResult=new MvcResultHandle(result).getResult(User.class);
		assertThat(userResult.getMessages(), hasSize(5));
		verify(service).getMessageByCondition(user, condition);
	}

	@Test
	public void testGetMessageById() throws Exception {
		Message message=new Message();
		message.setId(112);
		
		when(service.getMessageById(112)).thenReturn(message);
		
		MvcResult result=mvc.perform(get("/messages/112")
        		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andReturn();
        Message message2=new MvcResultHandle(result).getResult(Message.class);
        assertThat(message2.getId(), equalTo(112));
        
        Message message3=new Message(new Error("失败", "402"));
        when(service.getMessageById(0)).thenReturn(message3);
        
        result=mvc.perform(get("/messages/0")
        		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andReturn();
        message=new MvcResultHandle(result).getResult(Message.class);
	    assertThat(message.getError().getErrorCode(),equalTo("402"));
	   
	    verify(service).getMessageById(112);
	    verify(service).getMessageById(0);
	    
	}

}
