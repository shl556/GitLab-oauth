package com.shl.mongodb.spring;

import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

//默认使用英语，可以通过Document注解指定使用的语言
//@Document(language = "spanish")
@Document
public class TextIndex {
    
	//以该字段建立文本索引
	@TextIndexed
	private String hobby;
	//weight设置权重，影响最后查找时的排序结果
	@TextIndexed(weight=3)
	private String name;
	
	public TextIndex(String hobby,String name){
		this.hobby=hobby;
		this.name=name;
	}
	
	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return hobby+" : "+name;
	}

}
