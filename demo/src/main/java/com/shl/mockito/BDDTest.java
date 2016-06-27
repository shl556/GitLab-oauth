package com.shl.mockito;

import org.junit.Test;
import org.mockito.BDDMockito;

import com.shl.mockito.inject.A;

import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
public class BDDTest {
	
	/*
	 * BDDMockito继承自Mockito，在Mockito的基础上增加对了BDD模式的语法的支持
	 * 
	 * BDD相比TDD最大的区别在于其代码良好的可阅读性
	 */
	@Test
	public void test(){
		A mock=mock(A.class);
		
		//given相当于when，表示假定
		given(mock.doSomething()).willReturn(true);
		willReturn(true).given(mock).doSomething2();
		
		boolean result=mock.doSomething();
		boolean result2=mock.doSomething2();
		boolean result3=mock.doSomething2();
		
		assertTrue(result);
		assertTrue(result2);
		
		verify(mock).doSomething();
        then(mock).should(times(2)).doSomething2();		
		
	}

}
