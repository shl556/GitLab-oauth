package com.shl.sparkRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class UserRestClient {
    @Autowired
    private  RestTemplate template;
    
    private  Logger logger=LoggerFactory.getLogger(getClass());
    public int  getUsers(){
       List<User> users=template.getForObject("http://localhost:4567/users",new ArrayList<User>().getClass());
       int sum=users.size();
       logger.info("共查询到用户{}个",sum);
       return sum;
    }
    public int  getUsers2(){
    	List<User> users=template.getForObject("http://localhost:4568/users",new ArrayList<User>().getClass());
    	int sum=users.size();
    	logger.info("共查询到用户{}个",sum);
    	return sum;
    }

    public User  getUserById(int id){
        Map<String,Integer> params=new HashMap<>();
        params.put("id", id);
//    	User user=template.getForObject("http://localhost:4567/users/{id}", User.class, 3);
//    	User user=template.getForObject("http://localhost:4567/users/{id}", User.class, params);
        
        ResponseEntity<User> entity=template.getForEntity("http://localhost:4567/users/{id}", User.class, id);
        User user=entity.getBody();
        if (user.getError()!=null) {
        	logger.error(user.getError().toString());
		}else {
			logger.info("查询用户成功，{}",id,user);        
		}
        return user;
    }
    
    public User updateUsers(User user){
    	//注意所有的参数必须是string或者Resource形式
    	MultiValueMap<String, Object> form= new LinkedMultiValueMap<>();
    	form.add("userName", user.getUserName());
    	form.add("address", user.getAddress());
    	form.add("age", new Integer(user.getAge()).toString());
    	form.add("password", user.getPassword());
    	form.add("id", new Integer(user.getId()).toString());
    	
    	//可以添加file上传文件
//    	form.add("image", new ClassPathResource("test.img"));
    	
//    	User user=template.postForObject("http://localhost:4567/users",form, User.class);
    	
    	//可以通过httpEntity添加HttpHeaders
    	HttpEntity<MultiValueMap<String, Object>> entity=new HttpEntity<MultiValueMap<String,Object>>(form);
    	User result=template.postForObject("http://localhost:4567/users",entity, User.class);
    	if (result.getError()!=null) {
        	logger.error(result.getError().toString());
		}else{
    	   logger.info("修改用户信息成功，修改后的用户信息：{}",result);
		}
    	return result;
    }
    
    public int deleteUsers(int id){
    	ResponseEntity<User> result=template.exchange("http://localhost:4567/users/{id}", HttpMethod.DELETE, null,User.class, id);
        User user=result.getBody();
        if (user.getError()!=null) {
        	logger.error(user.getError().toString());
        	return -1;
		}else {
			logger.info("删除id为{}的用户成功",id);
			return 1;
		}
    }
    
    public User createUser(User user){
    	MultiValueMap<String, Object> form= new LinkedMultiValueMap<>();
    	form.add("userName", user.getUserName());
    	form.add("address", user.getAddress());
    	form.add("age", new Integer(user.getAge()).toString());
    	form.add("password", user.getPassword());
    	HttpEntity<MultiValueMap<String, Object>> entity=new HttpEntity<MultiValueMap<String,Object>>(form);
    	
    	ResponseEntity<User> result=template.exchange("http://localhost:4567/users", HttpMethod.POST, entity,User.class);
    	User resultUser=result.getBody();
        if (resultUser.getError()!=null) {
        	logger.error(resultUser.getError().toString());
		}else {
			logger.info("新建用户成功，{}",resultUser);
		}
        return resultUser;
    }
}
