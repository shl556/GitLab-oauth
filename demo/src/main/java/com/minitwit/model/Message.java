package com.minitwit.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	private Integer id;
	
	private int userId;
	
	private String username;
	
	private String text;
	
	private Date pubDate;
	
	private String pubDateStr;
	
	private String gravatar;
	
	private User user;
	
	private Error error;
	
	public Message(){
		
	}
	
	public Message(int userId, String text) {
		super();
		this.userId = userId;
		this.text = text;
	}

	public Message(Error error){
		this.error=error;
	}
	public Error getError() {
		return error;
	}
	
	public void setError(Error error) {
		this.error = error;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
		if(pubDate != null) {
			pubDateStr = new SimpleDateFormat("yyyy-MM-dd @ HH:mm").format(pubDate);
		}
	}

	public String getPubDateStr() {
		return pubDateStr;
	}

	public String getGravatar() {
		return gravatar;
	}

	public void setGravatar(String gravatar) {
		this.gravatar = gravatar;
	}
	
	@Override
	public String toString() {
	      return "用户id："+userId+"  用户名："+username+"  消息内容："+text+"messageId: "+id+"  pubDate:"+pubDateStr;
	      
	}
	
	@Override
	public boolean equals(Object obj) {
		Message message=(Message)obj;
		return this.toString().equals(message.toString());
	}
}
