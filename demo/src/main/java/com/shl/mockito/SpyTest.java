package com.shl.mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.shl.mockito.inject.A;


public class SpyTest {
	
	/*
	 * spy与mock的区别在于spy可以自由调用对象的真实方法，mock可以通过thenCallRealMethod调用真实
	 * 方法但是似乎对于jdk中类支持不好。另外需要注意的是对spy打桩时when方法依然会执行真实方法，只不过返回的
	 * 结果是打桩的结果而采用doReturn方法时不会调用真实方法。
	 * 
	 * Mockito不会Mock final方法，也不能对其verify。另外当通过实例对象Spy时，Mockito保留的是原实例对象的一个副本，当对原实例操作时
	 * 并不会影响到spy对象，即spy并不知道原实例对象的变化。
	 * 
	 */
	@Test
	public void test(){
		ArrayList<String> spy=spy(ArrayList.class);
		ArrayList<String> mock=mock(ArrayList.class);
		
		spy.add("abc");
		spy.add("sdfs");
		spy.add("fjks");
		//返回3，调用真实方法
		System.out.println(spy.size());
		
		when(spy.size()).thenReturn(10);
		
		//返回10，打桩后的结果
		System.out.println(spy.size());
		
		verify(spy,times(3)).add(anyString());
		verify(spy,times(2)).size();
		
		
		mock.add("abc");
		mock.add("sdfs");
		mock.add("fjks");
		//返回0，没有打桩返回默认值
		System.out.println(mock.size());
		
		when(mock.size()).thenReturn(10);
		
		//返回10,打桩的结果
		System.out.println(mock.size());
		
		//thenCallRealMethod不支持jdk中类
		when(mock.size()).thenCallRealMethod();
//		mock.add("shl");
		//依然返回0
		System.out.println(mock.size());
		
		
		verify(mock,times(3)).add(anyString());
		verify(mock,times(3)).size();
	}
	
	@Test
	public void test2(){
		A spy=spy(A.class);
		//when方法会调用真实方法，但是返回结果依然是打桩的结果
		when(spy.doSomething()).thenReturn(false);
		assertFalse(spy.doSomething());
		
		//调用doReturn方法不会调用真实方法
		doReturn(false).when(spy).doSomething2();
		assertFalse(spy.doSomething2());
		
		A mock=mock(A.class);
		when(mock.doSomething()).thenReturn(false);
		assertFalse(mock.doSomething());
		
		when(mock.doSomething()).thenCallRealMethod();
		assertTrue(mock.doSomething());
		
		verify(spy).doSomething();
		verify(spy).doSomething2();
	}
	
	@Test
	public void test3(){
		   List list = new LinkedList();
		   List spy = spy(list);
           
		   try{
		   when(spy.get(0)).thenReturn("foo");
		   }catch(Exception e){
			   e.printStackTrace();
		   }

		   //不会抛出异常
		   doReturn("foo").when(spy).get(0);
		   
		   System.out.println(spy.get(0));
		   
		   List spy2=spy(List.class);
		   //也不会抛出异常
		   when(spy2.get(0)).thenReturn("no exception");
		   System.out.println(spy2.get(0));
	}
}
