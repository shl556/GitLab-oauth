package com.shl.mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;

import org.junit.Test;

public class InteractionTest {
	@Test
    public void test4(){
    	List<String> mockOne=mock(List.class);
    	List<String> mockTwo=mock(List.class);
    	List<String> mockThree=mock(List.class);

    	mockOne.add("one");
//        mockOne.add("three");
    	 verify(mockOne).add("one");

    	 verify(mockOne, never()).add("two");

    	 //验证其他mock对象没有使用过
    	 verifyZeroInteractions(mockTwo, mockThree);
    	 //检查Mock对象是否存在没有被验证过的行为,不推荐频繁使用，因为该方法比较费时
    	 verifyNoMoreInteractions(mockOne);
    }
}
