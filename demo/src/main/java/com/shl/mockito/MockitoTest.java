package com.shl.mockito;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.LinkedList;
import java.util.List;
import static org.mockito.Mockito.*;


import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.internal.matchers.StartsWith;

public class MockitoTest {
    @Test
	public void test(){
    	 LinkedList mockedList = mock(LinkedList.class);

    	 //打桩（录制）
//    	 when(mockedList.get(0)).thenReturn("first");
//    	 when(mockedList.get(1)).thenThrow(new RuntimeException());

    	 //除了Spy对象以外，两者之间没有明显的区别。
    	 doReturn("first").when(mockedList).get(0);
    	 doThrow(new RuntimeException()).when(mockedList).get(1);
    	 
    	 System.out.println(mockedList.get(0));
         
    	 try{
    	 System.out.println(mockedList.get(1));
    	 }catch(RuntimeException e){
    		 e.printStackTrace();
    	 }

    	 //返回first，录制后再没有覆写的情况下调用多少次都会返回录制的结果
    	 System.out.println(mockedList.get(0));

    	 when(mockedList.get(0)).thenReturn("second");
    	 //返回second
    	 System.out.println(mockedList.get(0));
    	 
    	 //没有录制返回null
    	 System.out.println(mockedList.get(999));
    	 //返回false
    	 System.out.println(mockedList.isEmpty());
    	 //返回0
    	 //默认情况下所有有返回值的方法Mockito都会返回null，0，false等
    	 System.out.println(mockedList.size());

    	 
//    	 verify(mockedList).get(0);
    	 verify(mockedList, times(3)).get(0);
    }
    
}
