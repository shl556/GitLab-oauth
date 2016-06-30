package com.minitwit.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import javax.servlet.http.Cookie;

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
import com.minitwit.model.User;
import com.minitwit.util.PasswordUtil;

//standaloneSetup模式下以下注解都可以省略，即不运行外在依赖环境，适合测试没有依赖的Controller.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class,MvcConfig.class})
@WebAppConfiguration
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {

	private MockMvc mvc;
	private final String MEDIATYPE=MediaType.APPLICATION_JSON_UTF8_VALUE;
	@Autowired
	private WebApplicationContext wac;
	
	@Before
	public void setup(){
		//standaloneSetup情况下只会初始化UserController,不会初始化service等服务，没有多大的实际测试意义
//		mvc=MockMvcBuilders.standaloneSetup(new UserController()).build();
		mvc=MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	@Test
	public void testGetUserbyUsername() throws Exception {
		
		mvc.perform(get("/users/{username}", "user002")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(request().sessionAttribute("user", hasProperty("username", equalTo("user002"))))
		        .andExpect(request().sessionAttribute("user", hasProperty("id", equalTo(2))));
	  
		mvc.perform(get("/users/user0")
		          .accept(MEDIATYPE))
	              .andExpect(request().sessionAttribute("user", nullValue()));
	}

	@Test
	public void testInsertFollower() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("follower.id", "1");
		params.add("followee.id", "3");
		MvcResult result=mvc.perform(put("/users/follower")
				            .params(params)
				            .accept(MEDIATYPE))
		                    .andReturn();
		Error error=new MvcResultHandle(result).getResult(Error.class);
		assertNull(error);
		
		result=mvc.perform(post("/users/follower")
				.params(params)
	            .accept(MEDIATYPE))
                .andReturn();
		error=new MvcResultHandle(result).getResult(Error.class);
        assertThat(error.getErrorCode(), equalTo("200"));
		
	}

	@Test
	public void testDeleteFollower() throws Exception {
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("follower.id", "1");
		params.add("followee.id", "2");
		MvcResult result=mvc.perform(post("/users/follower")
				.params(params)
	            .accept(MEDIATYPE))
                .andReturn();
		Error error=new MvcResultHandle(result).getResult(Error.class);
        assertThat(error.getErrorCode(), equalTo("200"));
        
        result=mvc.perform(delete("/users/follower")
        		.params(params)
        		.accept(MEDIATYPE))
        		.andReturn();
        error=new MvcResultHandle(result).getResult(Error.class);
        assertNull(error);
        
        result=mvc.perform(post("/users/follower")
				.params(params)
	            .accept(MEDIATYPE))
                .andReturn();
		error=new MvcResultHandle(result).getResult(Error.class);
        assertThat(error.getErrorCode(), equalTo("404"));
        
	}

	
	@Test
	public void testRegisterUser() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("email", "123@h3c.com");
		params.add("username", "shl");
		params.add("password", "123456");
		
		mvc.perform(put("/users")
				        .accept(MEDIATYPE)
				        .params(params))
				        .andExpect(content().string(""));
		
		mvc.perform(get("/users/shl")
				         .accept(MEDIATYPE))
				         .andExpect(request().sessionAttribute("user", hasProperty("username", equalTo("shl"))))
				         .andExpect(request().sessionAttribute("user", hasProperty("password", equalTo("123456"))));
		
		
	}

	@Test
	public void testAutoLogin() throws Exception{
		MvcResult result=mvc.perform(get("/users/user002")
				        .accept(MEDIATYPE))
				        .andReturn();
		User user =new MvcResultHandle(result).getResult(User.class);
		
		String cookie="user002-"+user.getPassword();
		mvc.perform(get("/users/autologin")
				.accept(MEDIATYPE)
				.cookie(new Cookie("autologin", cookie)))
		        .andExpect(request().sessionAttribute("user",hasProperty("username", equalTo("user002"))))
		        .andExpect(request().sessionAttribute("user",hasProperty("id", equalTo(2))));
		
		cookie=cookie+"false";
		mvc.perform(get("/users/autologin")
				.accept(MEDIATYPE)
				.cookie(new Cookie("autologin", cookie)))
		        .andExpect(request().sessionAttribute("user",hasProperty("error", hasProperty("errorCode", equalTo("402")))));
		
	}

	@Test
	public void testCheckUser() throws Exception{
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "user002");
		params.add("password","user002");
		
		mvc.perform(post("/users/login")
				.accept(MEDIATYPE)
				.params(params))
		        .andExpect(request().sessionAttribute("user",hasProperty("username", equalTo("user002"))))
		        .andExpect(request().sessionAttribute("user",hasProperty("id", equalTo(2))));
	    
		params.set("password", "123456");
		mvc.perform(post("/users/login")
				.accept(MEDIATYPE)
				.params(params))
		        .andExpect(request().sessionAttribute("user",hasProperty("username", isEmptyOrNullString())));
	}

}
