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
public class ParameterizedTest {
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }  
           });
    }

//    private int fInput;
//
//    private int fExpected;
//
//    public ParameterizedTest(int input, int expected) {
//        fInput= input;
//        fExpected= expected;
//    }
    
    //通过Parameter注解的方式注入测试参数，value参数表示参数位置，fInput对应第一个参数
    @Parameter 
    public  int fInput;

    @Parameter(value=1)
    public  int fExpected;

    @Test
    public void test() {
        assertEquals(fExpected, Fibonacci.compute(fInput));
    }
}
