package com.shl.junit.category;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class A {
	@Test
	@Category(FastTests.class)
    public void test(){
    	System.out.println("A执行测试FastTests1");
    }
	
	@Test
	@Category(SlowTests.class)
    public void test2(){
    	System.out.println("A执行测试SlowTests1");
    }
	
	@Test
	@Category(FastTests.class)
    public void test3(){
    	System.out.println("A执行测试FastTests2");
    }
	
	@Test
	@Category(FastTests.class)
    public void test4(){
    	System.out.println("A执行测试FastTests3");
    }
	
	@Test
    public void test5(){
    	System.out.println("A执行测试");
    }
	
	@Test
    public void test6(){
    	System.out.println("A执行测试");
    }
}
