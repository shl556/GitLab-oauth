package com.shl.mockito.inject;

public class A {
    public String getName(){
    	return "abcd";
    }
    
    public boolean doSomething(){
    	System.out.println("调用A的doSomething方法");
    	return true;
    }
    public boolean doSomething2(){
    	System.out.println("调用A的doSomething方法2");
    	return true;
    }
    
    public String getName(String name){
    	return name+"sb";
    }
}
