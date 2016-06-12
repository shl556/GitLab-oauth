package com.spark.helloword;

public class User {
     private String userName;
     private String address;
     private int age;
     private String password;
     
	public User(String userName, String address, int age, String password) {
		super();
		this.userName = userName;
		this.address = address;
		this.age = age;
		this.password = password;
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
     
}
