package com.minitwit.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MvcResultHandle{
    private MvcResult result;
    public MvcResultHandle(MvcResult result){
    	this.result=result;
    }
    
    public MvcResultHandle(){
    	
    }
    
//    public Class<T> getGenericSuperClass( ) {
//    
//    	Type type = getClass().getGenericSuperclass();
//    	if(!(type instanceof ParameterizedType)){
//    	    type = getClass().getSuperclass().getGenericSuperclass();
//    	}
//    	Class<T> cls = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
//        return cls;
//    }
    
    public <T> T getResult(Class<T> targetClass){
    	String response="";
		try {
			response = result.getResponse().getContentAsString();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			throw new RuntimeException("json编码格式"+result.getResponse().getCharacterEncoding()+"不支持",e1);
		}
    	System.out.println("返回的json字符串： "+response);
    	if(StringUtils.isEmpty(response)){
    	     return null;
    	}
    	ObjectMapper mapper=new ObjectMapper();
    	T target=null;
    	try {
    		target=(T) mapper.readValue(response,targetClass);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("转换Json数据失败", e);
		}
    	return target;
    }
   
}
