package com.shl.mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.shl.mockito.inject.A;
import com.shl.mockito.inject.B;
import com.shl.mockito.inject.C3;

@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationsTest2 {

	@Mock(name="a")
	private A a;
	@Mock(name="b")
	private B b;
	
	/*
	 * InjectMocks表示将Mock对象或者Spy对象注入到指定对象中，产生的对象不是Mock对象，不能调用verify方法
	 * 验证。
	 * 
	 * 注入方式包括构造器注入，setter注入，属性注入三种注入方式，依次执行直到有一种成功为止，注入时按照参数类型匹配
	 * 参数，如果存在多个类型相同的参数则根据Mock的name属性确定注入的参数对象。需要注意，InjectMocks注入失败时
	 * 不会报错，需要手动检测注入成功与否。
	 * 
	 * 注入时所有的参数对象必须都是Mock对象，否则注入失败,如C4
	 */
	@InjectMocks
//	private C c;//构造器注入
//	private C2 c;//setter注入
	private C3 c;//属性注入
	

	@Test
	public void test(){
	    when(a.getName()).thenReturn("shl");
	    when(b.getName()).thenReturn("sun");
	    
	    System.out.println(c==null);
	    System.out.println(c.getName());
	    
	    verify(a).getName();
	    verify(b).getName();
//	    verify(c).getName();
	    
	}
	
}
