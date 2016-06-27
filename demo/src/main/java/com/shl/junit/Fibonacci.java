package com.shl.junit;
public class Fibonacci {
	private String result;
	
    public static int compute(int n) {
        int result = 0;

        if (n <= 1) { 
            result = n; 
        } else { 
            result = compute(n - 1) + compute(n - 2); 
        }

        return result;
    }
    
    public static int compute2(int a,int b,int c){
    	return a+b+c;
    }
    
    public String getResult() {
		return result;
	}
    
    public void setResult(String result) {
		this.result = result;
	}
}