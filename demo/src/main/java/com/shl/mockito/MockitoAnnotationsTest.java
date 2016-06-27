package com.shl.mockito;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationsTest {
	@Mock
    private ArrayList<String> mock;
	
	@Captor
	private ArgumentCaptor<Integer> captor;
	
	@Spy
	private ArrayList<String> spy;
	
//	@Before
//	public void init(){
//		MockitoAnnotations.initMocks(this);
//	}
	
//	@Rule
//	public MockitoRule rule = MockitoJUnit.rule();

	
	@Test
	public void test(){
		when(mock.get(0)).thenReturn("模拟测试");
		System.out.println(mock.get(0));
		verify(mock).get(0);
		
		System.out.println(spy.size());
		
		verify(mock).get(captor.capture());
		verify(spy).size();
		Integer arg=captor.getValue();
	    assertThat(arg,Matchers.equalTo(0));
	}
	
	
}
