package com.shl.sprintTest.annonation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.shl.sprintTest.annonation.SpringConfig;


@RunWith(SpringRunner.class)
//@ContextConfiguration(classes=SpringConfig.class)
@TransactionalDevTest//通过自定义注解减少重复配置
public class SpringJunit4Demo {
	
	//表示只有当前的系统环境或者配置变量符合要求才会执行测试，否则自动跳过，类似于Junit中的Assume
	//系统环境和配置变量通过@ProfileValueSourceConfiguration声明的类读取，如果没有配置则使用SystemProfileValueSource
	@IfProfileValue(name="java.vendor", value="Oracle Corporation")
	@Test
	public void test2(){
		
	}
	
	//与Junit中Test注解的timeout参数相比，Timed不会因为程序的运行时间超过设定的时间而终止程序运行，会等待程序运行结束
	@Timed(millis=100)
	@Repeat(10)//设定重复运行次数
	@Test
	public void test3(){
	   System.out.println("spring 超时测试开始");
	   try {
		Thread.currentThread().sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   System.out.println("spring 超时测试结束");
	}
	
	@Test(timeout=100)
	public void test32(){
		  System.out.println("spring 超时测试开始");
		   try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println("spring 超时测试结束");
	}
	public void test(){
		
	}
	
	

}
