package com.shl.mockito.inject;

public class C {
    private A a;
    private B b;
    
    public C(A a, B b){
    	this.a=a;
    	this.b=b;
    }
    
    public String getName(){
    	return a.getName()+" : "+b.getName();
    }
}
