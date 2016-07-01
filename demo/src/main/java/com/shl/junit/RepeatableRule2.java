package com.shl.junit;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

class RepeatableRule2 implements TestRule{ 
	private static int sum;
	private static int readSum=0;
	private static int hasTest=0;
    
	@Override
	public Statement apply(Statement base, Description description) {
        if(readSum==0){
            sum=getTestSum(description);
        	readSum=1;
//            System.out.println("读取Sum"+sum);
            System.out.println("执行BeforeClass方法");
        }
        
    	return new Statement() {  
         @Override  
         public void evaluate() throws Throwable {
        	hasTest++;
        	System.out.println("执行Before方法");
            base.evaluate();
            System.out.println("执行After方法");
            
            if(readSum==1&&hasTest==sum){
//            	System.out.println("执行AfterClass,hasTest:"+hasTest);
            	System.out.println("执行AfterClass方法");
            }
         }
	  };
	}
	
	private int getTestSum(Description description){
		Method[] methods= description.getTestClass().getDeclaredMethods();
		int sum=0;
		for(Method method:methods){
			Test[] tests=method.getAnnotationsByType(Test.class);
			if(tests.length!=0) {
				sum++;
			}
		}
		return sum;
	}
}  
