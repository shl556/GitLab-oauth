package com.shl.junit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnnonationTest {
    @BeforeClass
	public static void test(){
		System.out.println("整个测试类运行过程开始运行一次，用于资源的初始化");
	}
    @Before
	public void test2(){
		System.out.println("每个test方法执行前运行一次");
	}
    @Test
	public void test3(){
		System.out.println("执行单元测试");
	}
    @Test
    public void test32(){
    	System.out.println("执行单元测试2");
    }
    @Test
    public void test33(){
    	System.out.println("执行单元测试3");
    }
    @After
	public void test4(){
    	System.out.println("每个test方法执行后运行一次，无论Before或者Test方法执行过程中抛出异常，after方法都会执行");
	}
    @AfterClass
	public static void test5(){
    	System.out.println("整个测试类运行结束后运行一次，用于回收释放资源");
	}
}
