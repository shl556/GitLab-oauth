package com.shl.mongodb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
	private String id;
    private String username;
    private int age;
    private Date birthday;
    private String address;
    private String password;
    private int score;
    
    public void setId(String id) {
		this.id = id;
	}
    
    public String getId() {
		return id;
	}
    
    public int getScore() {
		return score;
	}
    
    public void setScore(int score) {
		this.score = score;
	}
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    public Map<String, Object> getMap(){
    	Map<String, Object> map=new HashMap<>();
    	map.put("username", username);
    	map.put("age", age);
    	map.put("birthday",birthday);
    	map.put("address", address);
    	map.put("score", score);
    	map.put("password", password);
    	return map;
    }
    
    @Override
    public String toString() {
    	return username+" "+address+" "+birthday;
    }
}
