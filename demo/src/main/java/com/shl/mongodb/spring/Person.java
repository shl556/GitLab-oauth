package com.shl.mongodb.spring;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Person {
	@Id
	private String id;
	  private String name;
	  private int age;
      @Version//提供版本控制乐观锁
	  private Long version;
      
      
	  public Person(String name, int age) {
	    this.name = name;
	    this.age = age;
	  }

	  public Long getVersion() {
		return version;
	}
	  
	  public void setVersion(Long version) {
		this.version = version;
	}
	  
	  public String getId() {
	    return id;
	  }
	  public String getName() {
	    return name;
	  }
	  public int getAge() {
	    return age;
	  }

	  public void setName(String name) {
		this.name = name;
	}
	  
	  public void setAge(int age) {
		this.age = age;
	}
	  
	  
	  @Override
	  public String toString() {
	    return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
	  }
}
