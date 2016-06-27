package com.shl.mockito.inject;

public class C2 {
    private A a;
    private B b;
    
    
    
    public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}


	public String getName(){
    	return a.getName()+" : "+b.getName();
    }
}
