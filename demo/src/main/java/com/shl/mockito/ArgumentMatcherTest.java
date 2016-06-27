package com.shl.mockito;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.StartsWith;
import org.mockito.verification.Timeout;

public class ArgumentMatcherTest {
	 @Test
	    public void test(){
	    	 LinkedList mockedList = mock(LinkedList.class); 
	         /*
	          *Matchers类表示Stubbing和Verify阶段的参数匹配规则，AdditionalMatchers是提供跟EasyMock类似的匹配规则，影响可读性
	          *不推荐使用。 Mockito类继承自Matchers类，以减少引入的类。
	          *
	          * Matchers除了常规的AnyInt、contains、eq等常规匹配规则外，可以通过argThat()和ArgumentMatcher自定义参数匹配规则，
	          * Mockito自身提供了各种常用的实现。需要注意的是如果采用参数匹配，则该方法的所有参数都必须使用参数匹配规则。
	          * 
	          * ArgumentCaptor用来捕获模拟对象调用指定方法时的参数
	          */
	 		
//	    	 when(mockedList.get(anyInt())).thenReturn("element");
	    	 when(mockedList.get(eq(12))).thenReturn("参数12");
	    	 //始终报nullPointerException
//	    	 GreaterThan<Integer> greaterThan=new GreaterThan<>(50);
//	         when(mockedList.get(argThat(greaterThan))).thenReturn("大于50");
	    	 
	         when(mockedList.contains(argThat(new StartsWith("ab")))).thenReturn(false);
	    	 
	    	 when(mockedList.get(AdditionalMatchers.geq(50))).thenReturn("大于50");
	    	//始终报nullPointerException
//	    	 when(mockedList.get(MockitoHamcrest.argThat(Matchers.greaterThan(100)))).thenReturn("大于100");
	    	 
	    	 System.out.println(mockedList.get(12));
	    	 System.out.println(mockedList.get(60));
//	    	 System.out.println(mockedList.get(200));
	         System.out.println(mockedList.contains("abcdsf"));
	    	 
	         //表示只调用一次
	         verify(mockedList).get(eq(60));
	    	 verify(mockedList,times(2)).get(anyInt());
//	    	 verify(mockedList,atLeast(2)).get(anyInt());
//	    	 verify(mockedList,atLeastOnce()).get(anyInt());
//	    	 verify(mockedList,never()).get(anyInt());
//	    	 verify(mockedList,atMost(3)).get(anyInt());

	    	 verify(mockedList, timeout(100)).get(anyInt());
	    	 verify(mockedList, timeout(100).times(2)).get(anyInt());
	    	 verify(mockedList, new Timeout(100, atLeast(2))).get(anyInt());
	    	 //自定义参数匹配规则
	    	 ArgumentMatcher<String> matcher=new ArgumentMatcher<String>() {

	  			@Override
	  			public boolean matches(Object argument) {
	  				return ((String)argument).length()>5;
	  			}
	  		 };
//	  		 ArgumentMatcher<String> matcher2=input -> ((String)input).length()>5;
	    	 
	  		 verify(mockedList).contains(argThat(matcher));
	    	 
	    	 //ArgumentCaptor用来捕获模拟对象调用指定方法时的参数
	    	 ArgumentCaptor<Integer> captor=ArgumentCaptor.forClass(Integer.class);
	    	 verify(mockedList,times(2)).get(captor.capture());
	    	 List<Integer> args=captor.getAllValues();
	    	 assertThat(args, hasItems(12,60));
	    }
}
