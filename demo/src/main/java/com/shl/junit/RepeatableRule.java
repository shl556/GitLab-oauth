package com.shl.junit;


import java.util.Arrays;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

class RepeatableRule implements MethodRule{  
    //Loop times  
    int times=1;  
    //Loop methods  
    String[] testMethods = null;  
      
    RepeatableRule(int times, String[] testMethods){  
        this.times = times;  
        this.testMethods = testMethods;  
    }  
      
    /* Statement表示当前执行的测试
     * FrameworkMethod表示对当前测试方法的描述
     * @see org.junit.rules.MethodRule#apply(org.junit.runners.model.Statement, org.junit.runners.model.FrameworkMethod, java.lang.Object)
     */
    @Override  
    public Statement apply(final Statement base, final FrameworkMethod method, Object target) {  
      return new Statement() {  
         @Override  
         public void evaluate() throws Throwable { 
        	System.out.println("应用Rule");
            int loopTime = 1;  
//            int sum=method.getDeclaringClass().getDeclaredMethods().length;
//            System.out.println(sum);
            if(Arrays.asList(testMethods).contains(method.getName())){//test method name matched  
                loopTime = times;  
            } 
            System.out.println("执行循环测试开始");
            for(int i=0;i<loopTime;i++)//before() and after() are also executed  
                //执行实际测试方法
            	base.evaluate();  
            System.out.println("执行循环测试结束");
         }
            
      };  
    }  
}  
