package com.shl.junit.category;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({SlowTests.class,FastTests.class})
public class C {
	@Test
    public void  test(){
    	System.out.println("C执行测试");
    }
	
	@Test
    public void  test2(){
    	System.out.println("C执行测试2");
    }
	
	@Test
    public void  test3(){
    	System.out.println("C执行测试3");
    }
}
