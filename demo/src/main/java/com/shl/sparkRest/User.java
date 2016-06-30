package com.shl.sparkRest;

public class User {
	 private int  id;
     private String userName;
     private String address;
     private int age;
     private String password;
     private Error error;
     
    public User(){
    	
    }
    
	public User(int id,String userName, String address, int age, String password) {
		super();
		this.id=id;
		this.userName = userName;
		this.address = address;
		this.age = age;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
     
    public Error getError() {
		return error;
	}
	
    public void setError(Error error) {
		this.error = error;
	}
    
    @Override
    public String toString() {
    	return "用户基本信息："+userName+"/"+address+"/"+age;
    }
}
