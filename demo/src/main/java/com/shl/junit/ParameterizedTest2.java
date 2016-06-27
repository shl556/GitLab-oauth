package com.shl.junit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTest2 {
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 { 0, 0,0,1 }, { 1, 1,2,4 }, { 2, 1,4,7 }, { 3, 2,5,10 }, { 4, 3,1,8 }, { 5, 5,1,11 }, { 6, 8,2,16 }  
           });
    }

    
    //通过Parameter注解的方式注入测试参数，value参数表示参数位置，fInput对应第一个参数
    @Parameter 
    public  int a;
    @Parameter(value=1) 
    public  int b;
    @Parameter(value=2)
    public  int c;

    @Parameter(value=3)
    public  int result;

    @Test
    public void test() {
        assertEquals(result, Fibonacci.compute2(a, b, c));
    }
    
    @Test
    public void test2() {
    	assertEquals(result, Fibonacci.compute2(a,b,c));
    }
    
    
    //测试参数只有一个的情况
//    @Parameters
//    public static Iterable<? extends Object> data2() {
//        return Arrays.asList("first test", "second test");
//    }
//    
//    @Parameters
//    public static Object[] data3() {
//        return new Object[] { "first test", "second test" };
//    }
}
