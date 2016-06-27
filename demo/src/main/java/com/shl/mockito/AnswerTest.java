package com.shl.mockito;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.shl.mockito.inject.A;

public class AnswerTest {
	
	@Test
	public void test(){
		A mock=mock(A.class);
	when(mock.getName(anyString())).thenAnswer(new Answer() {
	     public Object answer(InvocationOnMock invocation) {
//	         String[] args = (String[]) invocation.getArguments();
	         String args=invocation.getArgument(0);
	    	 A mock = (A)invocation.getMock();
	    	 Method method=invocation.getMethod();
	         String results="";
//	    	 try {
	            //会报一长串异常，通过method的invoke方法或者直接通过getName方法都会报错
//				results= "called with arguments: " + args+", results are"+method.invoke(mock, args);
				results= "called with arguments: " + args+", method name is "+method.getName();
//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
	    	return results;
	     }
	 });

	 //the following prints "called with arguments: foo"
	 System.out.println(mock.getName("shl"));
	}
	
   public void test2(){
	   /*
	    * 指定调用未打桩方法时的行为，推荐使用ETURNS_SMART_NULLS，从2.0开始默认使用ETURNS_SMART_NULLS
	    * 
	    * RETURNS_SMART_NULLS表示首先尽可能返回原始值，比如原来返回String类型就是返回一个空字符串""，然后再返回SmartNull,SmartNull与NPE
	    * 相比更友好，会指出具体是哪一行调用了为打桩的方法。
	    * 
	    * 1.0默认会使用RETURNS_DEFAULTS，即返回null或者0或者NPE
	    * 
	    * 其他选项包括RETURNS_MOCKS， RETURNS_DEEP_STUBS，CALLS_REAL_METHODS，主要针对遗留代码，不推荐使用
	    */
	   
	   A mock=mock(A.class,  RETURNS_SMART_NULLS);
   }
}
