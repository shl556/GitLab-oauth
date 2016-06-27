package com.shl.junit.category;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class B {
	@Test
	@Category(SlowTests.class)
    public void test(){
    	System.out.println("B执行测试SlowTests1");
    }
	
	@Test
	@Category(FastTests.class)
	public void test2(){
		System.out.println("B执行测试FastTests");
	}
	
	@Test
	public void test3(){
		System.out.println("B执行测试");
	}
	
	@Test
	@Category(SlowTests.class)
	public void test4(){
		System.out.println("B执行测试SlowTests3");
	}
	
	@Test
	@Category(FastTests.class)
	public void test5(){
		System.out.println("B执行测试FastTests");
	}
	
	@Test
	public void test6(){
		System.out.println("B执行测试");
	}
}
