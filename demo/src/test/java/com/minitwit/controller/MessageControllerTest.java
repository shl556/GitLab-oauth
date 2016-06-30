package com.minitwit.controller;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.minitwit.config.AppConfig;
import com.minitwit.config.MvcConfig;
import com.minitwit.model.Error;
import com.minitwit.model.Message;
import com.minitwit.model.User;


@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(initializers=WebContextInitial.class,
//loader=AnnotationConfigWebContextLoader.class)
@ContextConfiguration(classes={AppConfig.class,MvcConfig.class})
@WebAppConfiguration
@Transactional
@ActiveProfiles("test")
public class MessageControllerTest{
    
	@Autowired
	private WebApplicationContext wac;
	//MockMvc相当于一个可以发起请求和接受响应的浏览器，核心方法只有一个perform方法，用于发起请求
    private	MockMvc mvc;
	
    @Before
    public void setup(){
    	this.mvc=MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    /*
     * MockMvcRequestBuilders用于构建各种请求方法的请求包括文件上传和异步请求，各静态方法返回值类型为RequestBuilder实现类
     * MockHttpServletRequestBuilder和MockMultipartHttpServletRequestBuilder为RequestBuilder的两个实现类，分别代表
     * 普通请求和文件上传请求，这两者提供了大量方法可以模拟请求中的请求头，请求体，参数，Session,Cookie等等。
     * 
     * MockMvc的perform方法返回一个ResultAction对象，代表对请求响应的处理，主要有三个方法，andDo(ResultHandler handler)自定义
     * 处理方法，	andExpect(ResultMatcher matcher)判断结果是否匹配各项条件，	andReturn()返回请求结果。
     * 
     * MockMvcResultMatchers的返回各种ResultMatcher实现类，能够匹配请求响应的方方面面，如ContentResultMatchers匹配响应体，CookieResultMatchers
     * 匹配cookie，HeaderResultMatchers匹配响应头，ViewResultMatchers匹配返回的视图对象等。
     * 
     * MockMvcResultHandlers返回ResultHandler的实现类，不过方法相对较少，主要是把返回的结果打印到控制台或者指定的输出流中
     * 
     * ResultHandler只有一个方法handle(MvcResult result),MvcResult表示请求结果，可以通过MvcResult返回请求内容和响应内容，处理
     * 请求的异常等等
     */
	@Test
	public void testGetUserTimelineMessages() throws Exception {
		int id=1;
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
        MvcResult result=mvc.perform(get("/messages/{id}/pub", id)
        		                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        		                    .sessionAttr("user", user))//发起请求
                            .andReturn();//结果处理
        User user2=new MvcResultHandle(result).getResult(User.class);
        assertThat(user2.getMessages(), hasSize(28));
        assertThat(user2.getUsername(), equalTo("user002"));
        
        MvcResult result2=mvc.perform(get("/messages/12/pub")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .sessionAttr("user", user))//发起请求
        .andReturn();//结果处理
      User user3=new MvcResultHandle(result2).getResult(User.class);
      assertThat(user3.getMessages(), empty());
      assertThat(user3.getUsername(), equalTo("user002"));
      
	}

	@Test
	public void testGetUserFullTimelineMessages() throws Exception {
		int id=1;
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
        MvcResult result=mvc.perform(get("/messages/{id}/reply", id)
        		                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        		                    .sessionAttr("user", user))//发起请求
                            .andReturn();//结果处理
        User user2=new MvcResultHandle(result).getResult(User.class);
        assertThat(user2.getMessages(), hasSize(140));
        assertThat(user2.getUsername(), equalTo("user002"));
        
        MvcResult result2=mvc.perform(get("/messages/12/reply")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .sessionAttr("user", user))//发起请求
        .andReturn();//结果处理
      User user3=new MvcResultHandle(result2).getResult(User.class);
      assertThat(user3.getMessages(), empty());
      assertThat(user3.getUsername(), equalTo("user002"));
	
	}

	@Test
	public void testGetPublicTimelineMessages() throws Exception {
		List<Message> messages=new ArrayList<>();
		MvcResult result=mvc.perform(get("/messages").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				         .andReturn();
		
		messages=new MvcResultHandle(result).getResult(messages.getClass());
		assertThat(messages, hasSize(213));
	}

	@Test
	public void testInsertMessage() throws Exception {
//		MvcResult result=mvc.perform(put("/messages")
//				        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
////				        .requestAttr("message", null))//requestAttr必须要求非空
//				        .andReturn();
//	    Error error=new MvcResultHandle(result).getResult(Error.class);
//	    assertThat(error.getErrorCode(), equalTo("402"));
	    
		//采用Request attribute的方式模拟
//	    Message message=new Message();
//	    message.setPubDate(new Date());
//	    message.setText("测试");
//	    message.setUserId(1);
//	    
//	    MvcResult result=mvc.perform(put("/messages")
//	    		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
//	    		.requestAttr("message", message))
//	    		.andReturn();
//	    Error error=new MvcResultHandle(result).getResult(Error.class);
//	    assertNull(error);
	    
		//采用类似表单参数的方式模式
	    MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
	    params.add("text", "测试");
	    params.add("userId", "1");
	    
	    MvcResult result=mvc.perform(put("/messages")
	    		         .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
	    		         .params(params))
	    		         .andReturn();
	    Error error=new MvcResultHandle(result).getResult(Error.class);
	    assertNull(error);
	    
	    
	    User user =new User();
	    user.setId(2);
	    user.setUsername("user002");
	    result=mvc.perform(get("/messages/1/pub")
	    		           .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
	    		           .sessionAttr("user", user))
	    		           .andReturn();
	    User userResutl=new MvcResultHandle(result).getResult(User.class);
	    assertThat(user.getMessages(), hasSize(29));
	    
	}

	@Test
	public void testDeleteMessage() throws Exception {
		MvcResult result=mvc.perform(delete("/messages/112")
				                     .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				             .andReturn();
		Error error=new MvcResultHandle(result).getResult(Error.class);
	    assertNull(error);
	    
	    List<Message> messages=new ArrayList<>();
		result=mvc.perform(get("/messages").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				         .andReturn();
		
		messages=new MvcResultHandle(result).getResult(messages.getClass());
		assertThat(messages, hasSize(212));
		
		result=mvc.perform(delete("/messages/0")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                  .andReturn();
        error=new MvcResultHandle(result).getResult(Error.class);
        assertThat(error.getErrorCode(), equalTo("402"));
	    
	}

	@Test
	public void testUpdateMessage() throws Exception {
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("id", "112");
        params.add("text", "测试");
        
        MvcResult result=mvc.perform(post("/messages")
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                 .params(params))
        .andReturn();
        Error error=new MvcResultHandle(result).getResult(Error.class);
         assertNull(error);
         
        result=mvc.perform(get("/messages/112")
        		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andReturn();
        Message message=new MvcResultHandle(result).getResult(Message.class);
        assertThat(message.getText(), equalTo("测试"));
	}

	@Test
	public void testGetMessageByCondition() throws Exception {
		User user=new User();
		user.setId(2);
		user.setUsername("user002");
		
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
		
		assertThat(userResult.getMessages(), hasSize(6));
	}

	@Test
	public void testGetMessageById() throws Exception {
		MvcResult result=mvc.perform(get("/messages/112")
        		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andReturn();
        Message message=new MvcResultHandle(result).getResult(Message.class);
        assertThat(message.getId(), equalTo(112));
        
        result=mvc.perform(get("/messages/0")
        		.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        		.andReturn();
        message=new MvcResultHandle(result).getResult(Message.class);
	    assertThat(message.getError().getErrorCode(),equalTo("402"));
	}

}
