package com.shl.junit;

import java.util.Properties;
import static org.hamcrest.Matchers.*;
import org.junit.Assume;
import org.junit.Test;

public class AssumeTest {
	/*
	 * assume假设机制的意思是指定某个测试条件，只有该测试条件符合的情况下才会继续执行测试，否则跳出该测试方法
	 * 执行下一个测试方法，原测试方法被中止但是不抛出异常。这样做的意义在于避免某些不在测试范围内考虑的变量导致
	 * 测试失败。
	 */
	
    @Test  
    public void testIfVersioonGreaterThan4()  
    {  
    	System.out.println("assume测试开始");
        String javaVersion=getJavaVersion();
        Assume.assumeThat(javaVersion, equalTo("1.7"));  
        System.out.println("assume测试结束");  
    }  
    
    @Test
    public void test(){
    	System.out.println("正常测试");
    }

    private String getJavaVersion(){
    	Properties sys=System.getProperties();
    	String javaVersion=sys.getProperty("java.version");
    	String result=javaVersion.substring(0, javaVersion.lastIndexOf("."));
    	return result;
    }
}
