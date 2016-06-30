package com.minitwit.controller.mock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minitwit.model.Error;
import com.minitwit.model.User;

//@ContextConfiguration("/applicationContext.xml")
//@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

	/*
	 * RestTemplate支持所有的请求方法，内部默认通过JDK的net包中相关HTTP类实现，也可配置为通过Apache Httpclient实现。
	 * 
	 * RestTemplate对每种请求方法都支持多个重载版本，其中：
	 * RequestCallback表示对ClientHttpRequest处理的回调函数，内部由doExcute()方法调用；ResponseExtractor与之相反，
	 * 表示对响应进行处理的回调函数
	 * RequestEntity可用于构造请求方法，url，各种请求头，请求体等，可由构造器构造或者静态方法构造，由exchage方法封装
	 * ResponseEntity可用于构造请求的响应，同RequestEntity一样可以通过多种构造器或者静态方法构造
	 * HttpEntity是RequestEntity的父类，两者的方法基本相同
	 * 
	 * 核心方法是exchange方法，支持各种方法和请求参数，excute方法允许自定义对请求和响应处理的回调函数，剩下的针对各种请求的方法时基于
	 * exchange的封装，指定请求头参数上很不灵活。
	 */
	private RestTemplate template;
	private String baseUrl="http://localhost:8080";
	
	/*
	 * 用来模拟请求响应的模拟服务器，主要用三个方法，createServer完成模拟服务器初始化，
	   expect方法设置期望的请求及对应响应，返回一个ResponseActions对象，通过ResponseActions可以设置期望的响应
       verify方法验证是否处理过指定URL的请求
       
       expect方法有两个参数，其中ExpectedCount类表示对处理请求次数的匹配，RequestMatcher接口表示对请求的匹配，由MockRestRequestMatchers提供各种实现类
       expect方法返回ResponseActions对象，该对象有两个方法，andExcept()方法返回对象本身，参数为RequestMatcher,另一个方法
                为andRespond，参数为ResponseCreator接口实现类，用于自定义服务器响应，由MockRestResponseCreators的静态方法提供
	 *
	 */
	private MockRestServiceServer mockServer;
	
	private final MediaType MEDIATYPE=MediaType.APPLICATION_JSON_UTF8;
	private final String MEDIATYPESTRING=MediaType.APPLICATION_JSON_UTF8_VALUE;
	@Before
	public void SetupContext(){
		template=new RestTemplate();
		mockServer=MockRestServiceServer.createServer(template);
	}
	
	
	@Test
	public void testGetUserbyUsername() {
		User user=new User();
		user.setId(2);
		user.setEmail("123@h3c.com");
		user.setPassword("123456");
        user.setUsername("user002");
        String json=getJson(user);
        String targetUrl=baseUrl+"/users/user002";
        
		mockServer.expect(requestTo(targetUrl))
		          .andExpect(method(HttpMethod.GET))
		          .andExpect(content().contentType(MEDIATYPE))//设置服务器端期望的请求
		          .andRespond(withSuccess(json, MEDIATYPE));//设置服务器端期望的响应
	
		RequestEntity entity=RequestEntity.get(getUri(targetUrl)).header("Content-Type", MEDIATYPESTRING).build();
		//发起请求与服务器端交互
	    ResponseEntity<User> response=template.exchange(entity, User.class);
	    
	    User result=response.getBody();
	    //验证请求结果
	    assertThat(result.getUsername(), equalTo("user002"));
	    assertThat(result.getPassword(), equalTo("123456"));
	    
	    //验证服务器设置的请求是否被匹配
	    mockServer.verify();
	}

	@Test
	public void testInsertFollower() {
		String targetUri=baseUrl+"/users/follower";
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("id", "2");
		params.add("name", "3");
		
		mockServer.expect(requestTo(targetUri))
		          .andExpect(method(HttpMethod.PUT))
//		          .andExpect(content().contentType(MEDIATYPE))
		          .andExpect(header("Accept", MEDIATYPESTRING))
//		          .andExpect(content().string(params.toString()))
		          .andExpect(content().formData(params))//报错Request content expected:<{id=[2], name=[3]}> but was:<{{"id":["2"],"name":["3"]}=[null]}>
		                                                //formData内部通过FormHttpMessageConventer将Request body的字节数组转换成MultiValueMap,然后再比较两个MultiValueMap
		                                                //如果把Content-Type改成Accept则不会报错
		          .andRespond(withNoContent());
		
		//指定Headers的两种方式
//		MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
//		headers.add("Content-Type", MEDIATYPESTRING);
		HttpHeaders headers=new HttpHeaders();
//		headers.setContentType(MEDIATYPE);
		headers.setAccept(Arrays.asList(MEDIATYPE));
		
//		HttpEntity<String> entity=new HttpEntity<String>(params.toString(), headers);
		HttpEntity<MultiValueMap<String, String>> entity=new HttpEntity<>(params, headers);
		
//		RequestEntity<String> entity=RequestEntity.put(getUri(targetUri)).contentType(MEDIATYPE).body(params.toString());
//        ResponseEntity<Error> response=template.exchange(entity, Error.class);
        
        ResponseEntity<Error> response=template.exchange(getUri(targetUri), HttpMethod.PUT, entity, Error.class);
		Error error=response.getBody();
        
//		Error error=template.postForObject(targetUri, entity, Error.class);
		
		assertNull(error);
		
        mockServer.verify();
	}

	@Test
	public void testDeleteFollower() {
        String targetUri=baseUrl+"/users/follower";
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("id", "2");
		params.add("name", "3");
		
		mockServer.expect(requestTo(targetUri))
		          .andExpect(method(HttpMethod.DELETE))
		          .andExpect(content().contentType(MEDIATYPE))
		          .andExpect(content().string(params.toString()))
		          .andRespond(withNoContent());
		
		HttpHeaders headers=new HttpHeaders();
		headers.setContentType(MEDIATYPE);
		HttpEntity<String> entity=new HttpEntity<String>(params.toString(), headers);
        ResponseEntity<Error> response=template.exchange(targetUri, HttpMethod.DELETE, entity, Error.class);
        
		Error error=response.getBody();
        assertNull(error);
		
        mockServer.verify();
	}

	@Test
	public void testIsUserFollower() {
		 String targetUri=baseUrl+"/users/follower";
			
			MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
			params.add("id", "2");
			params.add("name", "3");
			
			Error error=new Error("匹配成功", "200");
			
			mockServer.expect(requestTo(targetUri))
			          .andExpect(method(HttpMethod.POST))
			          .andExpect(content().contentType(MEDIATYPE))
			          .andExpect(content().string(params.toString()))
			          .andRespond(withSuccess(getJson(error), MEDIATYPE));
			
			RequestEntity<String> entity=RequestEntity.post(getUri(targetUri)).contentType(MEDIATYPE).body(params.toString());
	        ResponseEntity<Error> response=template.exchange(entity, Error.class);
	        
			Error result=response.getBody();
	        assertThat(result.getErrorCode(), equalTo("200"));
			
	        mockServer.verify();
	}

	@Test
	public void testRegisterUser() {
		String targetUri=baseUrl+"/users";
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "shl");
		params.add("password", "123456");
		
		mockServer.expect(requestTo(targetUri))
		          .andExpect(method(HttpMethod.PUT))
		          .andExpect(content().string(params.toString()))
		          .andExpect(content().contentType(MEDIATYPE))
		          .andRespond(withNoContent());
		
		RequestEntity<String> entity=RequestEntity.put(getUri(targetUri)).contentType(MEDIATYPE).body(params.toString());
		
		ResponseEntity<Error> responseEntity=template.exchange(entity, Error.class);
		
		Error error=responseEntity.getBody();
		
		assertNull(error);
	}

	@Test
	public void testAutoLogin() {
		String targetUri=baseUrl+"/users/autologin";
		String cookie="user002-user002";
		mockServer.expect(requestTo(targetUri))
				  .andExpect(header("Cookie", cookie))
				  .andExpect(method(HttpMethod.GET))
				  .andExpect(content().contentType(MEDIATYPE))
				  .andRespond(withNoContent());
        
		RequestEntity entity=RequestEntity.get(getUri(targetUri)).header("Content-Type", MEDIATYPESTRING).header("Cookie", cookie).build();
		
		ResponseEntity<Error> responseEntity=template.exchange(entity, Error.class);
		
		Error error=responseEntity.getBody();
		
		assertNull(error);
	}

	@Test
	public void testCheckUser() {
		String targetUri=baseUrl+"/users/login";
		
		MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
		params.add("username", "user002");
		params.add("password", "123456");
		
		User user=new User();
		user.setUsername("user002");
		user.setPassword("123456");
		
		mockServer.expect(requestTo(targetUri))
		  .andExpect(method(HttpMethod.POST))
		  .andExpect(content().contentType(MEDIATYPE))
		  .andExpect(content().string(params.toString()))
		  .andRespond(withSuccess(getJson(user), MEDIATYPE));
		
        RequestEntity<String> entity=RequestEntity.post(getUri(targetUri)).contentType(MEDIATYPE).body(params.toString());
		
		ResponseEntity<User> responseEntity=template.exchange(entity, User.class);
		
		User result=responseEntity.getBody();
		
		assertThat(result.getUsername(), equalTo("user002"));
		assertThat(result.getPassword(), equalTo("123456"));
		
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
}
