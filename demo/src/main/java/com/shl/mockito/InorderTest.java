package com.shl.mockito;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

public class InorderTest {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
    public void test(){
		List singleMock = mock(List.class);

    	 singleMock.add("was added first");
    	 singleMock.add("was added second");

    	 InOrder inOrder = inOrder(singleMock);
         //验证同一Mock对象的方法调用顺序
    	 inOrder.verify(singleMock).add("was added first");
    	 inOrder.verify(singleMock).add("was added second");

    	 List firstMock = mock(List.class);
    	 List secondMock = mock(List.class);

    	 firstMock.add("was called first");
    	 secondMock.add("was called second");

    	 InOrder inOrder2 = inOrder(firstMock, secondMock);

    	 //验证多个Mock对象的方法调用顺序
    	 inOrder2.verify(firstMock).add("was called first");
    	 inOrder2.verify(secondMock).add("was called second");
    }
} 
