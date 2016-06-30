package com.shl.sparkRest;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserRestClientSpringMockTest {
	
	@Autowired
	private UserRestClient client;
	private MockRestServiceServer server;
    private String baseUri="http://localhost:4567/users";
	private final MediaType JSON=MediaType.APPLICATION_JSON_UTF8;
    
    @Before
    public void init(){
    	server=MockRestServiceServer.createServer(client.getTemplate());
    }
    
	@Test
	public void testGetUsers() {
		User user=new User(1,"shl", "北京", 24, "123456");
		User user2=new User(2,"shl2", "北京", 24, "123456");
		User user3=new User(3,"shl3", "北京", 24, "123456");
		User user4=new User(4,"shl4", "北京", 24, "123456");
		List<User> users=Arrays.asList(user,user2,user3,user4);
		
		server.expect(requestTo(baseUri))
		      .andExpect(method(HttpMethod.GET))
		      .andRespond(withSuccess(getJson(users), JSON));
		
		int num=client.getUsers();
	    assertEquals(num,4);
	    
	    server.verify();
	}

	
	@Test
	public void testGetUserById() {
		User user=new User(2,"shl2", "北京", 24, "123456");
		User user2=new User();
		user2.setError(new Error("id为7的用户找不到","401"));
		
		server.expect(requestTo(baseUri+"/2"))
		      .andExpect(method(HttpMethod.GET))
		      .andRespond(withSuccess(getJson(user), JSON));
		
		server.expect(requestTo(baseUri+"/5"))
		.andExpect(method(HttpMethod.GET))
		.andRespond(withSuccess(getJson(user2), JSON));
		
		User result=client.getUserById(2);
		assertThat(result.getUserName(), equalTo("shl2") );
		assertThat(result.getAge(), equalTo(24) );

		User result2=client.getUserById(5);
		assertThat(result2.getError().getErrorCode(),equalTo("401"));
	    
		server.verify();
	}

	@Test
	public void testUpdateUsers() {
		User user=new User(2, "shl5", "北京", 26, "123456");
		User user2=new User();
		user2.setError(new Error("id为7的用户找不到","401"));
		
		server.expect(requestTo(baseUri))
		      .andExpect(method(HttpMethod.POST))
		      .andExpect(content().formData(getParams(user)))
		      .andRespond(withSuccess(getJson(user),JSON));
		
		server.expect(requestTo(baseUri))
		.andExpect(method(HttpMethod.POST))
		.andExpect(content().formData(getParams(user2)))
		.andRespond(withSuccess(getJson(user2),JSON));
		    
		User result=client.updateUsers(user);
		assertThat(result.getUserName(),equalTo("shl5"));
		
		User result2=client.updateUsers(user2);
		assertThat(result2.getError().getErrorCode(), equalTo("401"));
		
		server.verify();
	}

	@Test
	public void testDeleteUsers() {
		User user=new User();
		User user2=new User();
		user2.setError(new Error("id为6的用户找不到","401"));
		
		server.expect(requestTo(baseUri+"/1"))
	      .andExpect(method(HttpMethod.DELETE))
	      .andRespond(withSuccess(getJson(user),JSON));
	
	    server.expect(requestTo(baseUri+"/6"))
	          .andExpect(method(HttpMethod.DELETE))
	          .andRespond(withSuccess(getJson(user2),JSON));
	    
	    int result=client.deleteUsers(1);
	    assertEquals(result, 1);
	    
	    int result2=client.deleteUsers(6);
	    assertEquals(result2, -1);
	    
	    server.verify();
	}

	
	@Test
	public void testCreateUser(){
		User result=new User(5, "shl5","北京", 27, "123456");
		User result2=new User();
		result2.setError(new Error("用户名已存在", "402"));
		
		User user=new User();
		user.setAddress("北京");
		user.setUserName("shl");
		user.setAge(26);
		user.setPassword("123456");
		
		User user2=new User();
		user2.setAddress("北京");
		user2.setUserName("sun");
		user2.setAge(26);
		user2.setPassword("123456");
		
		server.expect(requestTo(baseUri))
		      .andExpect(method(HttpMethod.PUT))
		      .andExpect(content().formData(getParams(user)))
		      .andRespond(withSuccess(getJson(result), JSON));
		
		server.expect(requestTo(baseUri))
		      .andExpect(method(HttpMethod.PUT))
		      .andExpect(content().formData(getParams(user2)))
		      .andRespond(withSuccess(getJson(result2), JSON));
		
		
		User resultUser=client.createUser(user);
		assertEquals(5, resultUser.getId());
		User resultUser2=client.createUser(user2);
		assertEquals("402", resultUser2.getError().getErrorCode());
		
		server.verify();
	}
	
	private <T> String getJson(T obj){
		ObjectMapper mapper=new ObjectMapper();
		String result="";
		try {
			result= mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("从实体类转换为json失败",e);
		}
		return result;
	}
	
	private URI getUri(String uri){
		URI result=null;
		try {
			result=new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("由String构造URI失败",e);
		}
		return result;
	}
	
	private MultiValueMap<String, String> getParams(User user){
		MultiValueMap<String, String> form= new LinkedMultiValueMap<>();
    	form.add("userName", user.getUserName());
    	form.add("address", user.getAddress());
    	form.add("age", new Integer(user.getAge()).toString());
    	form.add("password", user.getPassword());
    	form.add("id", new Integer(user.getId()).toString());
    	return form;
	}
}
