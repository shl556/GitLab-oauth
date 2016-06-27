package com.shl.mockito.inject;

public class C3 {
    private A a;
    private B b;
    
    
	public String getName(){
    	return a.getName()+" : "+b.getName();
    }
}
