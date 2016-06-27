package com.shl.mockito;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Test;

import com.shl.mockito.inject.A;


public class IteratorsStubTest {
	
	@Test
    public void test(){
    	A mock=mock(A.class);
    	//定制各次调用返回的结果
    	when(mock.getName(anyString())).thenThrow(new RuntimeException())
    	                .thenReturn("one")
    	                .thenReturn("two")
    	                .thenCallRealMethod()
    	                .thenThrow(new RuntimeException())
//    	                .thenThrow(new IOException())//报错提示该方法抛出的IOException是无效的
    	                .thenReturn("three","four","five");
    	
    	try {
			mock.getName("shl");
		} catch (Exception e) {
            System.out.println("第一次调用，返回RuntimeException");
		}
    	
    	System.out.println("第二次调用"+mock.getName("ab"));
    	System.out.println("第三次调用"+mock.getName("ab"));
    	System.out.println("第四次调用"+mock.getName("ab"));

    	try {
			mock.getName("shl");
		} catch (Exception e) {
            System.out.println("第五次调用，返回IoException");
		}
    	
    	System.out.println("第六次调用"+mock.getName("ab"));
    	System.out.println("第七次调用"+mock.getName("ab"));
    	System.out.println("第八次调用"+mock.getName("ab"));
    }
}
