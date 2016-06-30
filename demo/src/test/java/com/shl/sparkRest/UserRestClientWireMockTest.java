package com.shl.sparkRest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserRestClientWireMockTest {
	
	//从4.10以前注解ClassRule适用于public static的字段，注解Rule适用于public非static的字段，不能应用在static字段
    //Rule和	ClassRule注解都是对Before和Afer注解的封装，默认会实现junit的TestRule,MethodRule接口，通过该接口实现在
	//执行任何测试前先执行Before方法，执行测试后再执行After方法。
	//对于WireMockRule而言则是在执行测试前初始化WireMock,开启服务器，测试结束后关闭服务器,内部通过WireMock控制WireMockServer,WireMock相当于客户端
	//	@ClassRule
//	public static WireMockClassRule wireMockRule = new WireMockClassRule(8089);

	@Rule
//	public WireMockClassRule instanceRule = new WireMockClassRule(WireMockConfiguration.wireMockConfig().bindAddress("localhost").port(4567));
	public WireMockClassRule service = new WireMockClassRule(4567);
	@Rule
	public WireMockClassRule service2 = new WireMockClassRule(4568);
	
	@Autowired
	private UserRestClient client;
    private ObjectMapper mapper=new ObjectMapper();
	
	@Test
	public void testGetUsers() {
		User user=new User(1,"shl", "北京", 24, "123456");
		User user2=new User(2,"shl2", "北京", 24, "123456");
		User user3=new User(3,"shl3", "北京", 24, "123456");
		User user4=new User(4,"shl4", "北京", 24, "123456");
		List<User> users=Arrays.asList(user,user2,user3,user4);
		String json="";
		try {
			json=mapper.writeValueAsString(users);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//WireMockClassRule扩展在WireMockServer，可以通过该类直接进行stub
//		service.stubFor(mappingBuilder);
		//get方法是对MappingBuilder的封装，urlEqualTo方法是对UrlMatchingStrategy的封装
		//urlMatching("/thing/matching/[0-9]+")
		//urlPathEqualTo("/query")和urlPathMatching("/qu.*")针对待查询字符串的URL
		stubFor(
				get(urlEqualTo("/users")) //返回MappingBuild，MappingBuild是对RequestPatternBuilder和ResponseDefinitionBuilder的封装，可以设置请求体，请求头和查询字符串
//				get(urlEqualTo("/users")).atPriority(1) //设置匹配优先级，默认使用最近添加的stub匹配，数值越小优先级越高
//				.withQueryParam("search", containing("Some text"))
//				.withRequestBody(matching("<status>OK</status>"))//支持对json、xml、Xpath、contain匹配
//				.withHeader("Content-Type", equalTo("text/xml"))
				.willReturn(
	            	aResponse()//返回一个.ResponseDefinitionBuilder，定义响应体，响应头，响应状态
	            	.withHeader("Content-Type", "application/json")
	                .withBody(json)
//	                .withBodyFile("path/to/myfile.xml")//模拟文件下载，文件默认在src/java/resources下
	                ));
		
		assertEquals(client.getUsers(), 4);
		
		verify(getRequestedFor(urlEqualTo("/users")));
		
	}

	@Test
	public void testGetUsers2() {
		User user=new User(1,"shl", "北京", 24, "123456");
		User user2=new User(2,"shl2", "北京", 24, "123456");
		User user3=new User(3,"shl3", "北京", 24, "123456");
		User user4=new User(4,"shl4", "北京", 24, "123456");
		List<User> users=Arrays.asList(user,user2,user3,user4);
		String json="";
		try {
			json=mapper.writeValueAsString(users);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		service2.stubFor(
				get(urlEqualTo("/users"))
				.willReturn(
                 aResponse().withBody(json)
                 .withHeader("Content-Type", "application/json")
						)
				);
        assertEquals(client.getUsers2(), 4);
		
		service2.verify(getRequestedFor(urlEqualTo("/users")));
	}
	@Test
	public void testGetUserById() {
		User user=new User(2,"shl2", "北京", 24, "123456");
		User user2=new User();
		user2.setError(new Error("id为7的用户找不到","401"));
		
		String json="";
		String json2="";
		try {
			json=mapper.writeValueAsString(user);
			json2=mapper.writeValueAsString(user2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stubFor(
				get(urlEqualTo("/users/2"))
				.willReturn( 
						aResponse().withBody(json)
						.withHeader("Content-Type", "application/json")
						)
				);
		stubFor(
				get(urlEqualTo("/users/6"))
				.willReturn( 
						aResponse().withBody(json2)
						.withHeader("Content-Type", "application/json")
						)
				);
		
		User result = client.getUserById(2);
		
		assertEquals(result.getUserName(), "shl2");
		User result2= client.getUserById(6);
		assertEquals(result2.getError().getErrorCode(), "401");
	    
		verify(getRequestedFor(urlEqualTo("/users/2")));
		verify(getRequestedFor(urlEqualTo("/users/6")));
	}

	@Test
	public void testUpdateUsers() {
		User user=new User(2, "shl5", "北京", 26, "123456");
		User user2=new User();
		user2.setError(new Error("id为7的用户找不到","401"));
		
		String json="";
		String json2="";
		try {
			json=mapper.writeValueAsString(user);
			json2=mapper.writeValueAsString(user2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stubFor(
				post(urlEqualTo("/users"))
				.withRequestBody(containing("id=2"))
				.willReturn( 
						aResponse().withBody(json)
						.withHeader("Content-Type", "application/json")
						)
				);
		stubFor(
				post(urlEqualTo("/users"))
				.withRequestBody(containing("id=6"))
				.willReturn( 
						aResponse().withBody(json2)
						.withHeader("Content-Type", "application/json")
						)
				);
		
		User result=client.updateUsers(user);
		assertEquals(user.getUserName(), result.getUserName());
		User user3=new User(6, "shl5", "北京", 26, "123456");
		User result2=client.updateUsers(user3);
        assertEquals("401", result2.getError().getErrorCode());		
	    
        verify(2,postRequestedFor(urlEqualTo("/users")));
        List<LoggedRequest> requests=findAll(postRequestedFor(urlEqualTo("/users")));
        requests.forEach(s->System.out.println(s.getBodyAsString()));
	}

	@Test
	public void testDeleteUsers() {
		User user=new User();
		User user2=new User();
		user2.setError(new Error("id为6的用户找不到","401"));
		
		String json="";
		String json2="";
		try {
			json=mapper.writeValueAsString(user);
			json2=mapper.writeValueAsString(user2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stubFor(
				delete(urlEqualTo("/users/2"))
				.willReturn( 
						aResponse().withBody(json)
						.withHeader("Content-Type", "application/json")
						)
				);
		stubFor(
				delete(urlEqualTo("/users/6"))
				.willReturn( 
						aResponse().withBody(json2)
						.withHeader("Content-Type", "application/json")
						)
				);
		
		int result=client.deleteUsers(2);
		assertEquals(1, result);
		int result2=client.deleteUsers(6);
		assertEquals(-1, result2);
		
		verify(deleteRequestedFor(urlEqualTo("/users/2")));
		verify(deleteRequestedFor(urlEqualTo("/users/6")));
	}

	
	@Test
	public void testCreateUser(){
		User result=new User(5, "shl5","北京", 27, "123456");
		User result2=new User();
		result2.setError(new Error("用户名已存在", "402"));
		
		String json="";
		String json2="";
		try {
			json=mapper.writeValueAsString(result);
			json2=mapper.writeValueAsString(result2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stubFor(
				put(urlEqualTo("/users"))
				.withRequestBody(containing("userName=sun"))
				.willReturn( 
						aResponse().withBody(json)
						.withHeader("Content-Type", "application/json")
						)
				);
		stubFor(
				put(urlEqualTo("/users"))
				.withRequestBody(containing("userName=shl"))
				.willReturn( 
						aResponse().withBody(json2)
						.withHeader("Content-Type", "application/json")
						)
				);
		
		User user=new User();
		user.setAddress("北京");
		user.setUserName("shl");
		user.setAge(26);
		user.setPassword("123456");
		
		User user2=new User();
		user2.setAddress("北京");
		user2.setUserName("sun");
		//匹配第二个桩userName=shl
//		user2.setUserName("shl5");
		user2.setAge(26);
		user2.setPassword("123456");
		
		User resultUser=client.createUser(user);
		assertEquals("402", resultUser.getError().getErrorCode());
		User resultUser2=client.createUser(user2);
		assertEquals(5, resultUser2.getId());
	   
		verify(2,putRequestedFor(urlEqualTo("/users")));
        List<LoggedRequest> requests=findAll(putRequestedFor(urlEqualTo("/users")));
        requests.forEach(s->System.out.println(s.getBodyAsString()));
	}
}
