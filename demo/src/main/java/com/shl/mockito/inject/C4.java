package com.shl.mockito.inject;

public class C4 {
    private A a;
    private B b;
    private boolean inject;
    
    public C4(A a, B b,boolean inject){
    	this.a=a;
    	this.b=b;
    }
    
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

	public boolean isInject() {
		return inject;
	}
	
	public void setInject(boolean inject) {
		this.inject = inject;
	}

	public String getName(){
    	return a.getName()+" : "+b.getName();
    }
}
