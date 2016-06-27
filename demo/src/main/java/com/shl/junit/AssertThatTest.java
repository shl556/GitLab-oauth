package com.shl.junit;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
public class AssertThatTest {
    
	@SuppressWarnings("unchecked")
	@Test
	public void test(){
		//array 和 arrayContaining方法比较元素内容和顺序，即两者必须完全一致
		assertThat(new Integer[]{1,2,3}, is(array(equalTo(1), equalTo(2), equalTo(3))));
		assertThat(new Integer[]{1,2,3}, arrayContaining(1,2,3));
		assertThat(new Integer[]{1,2,3}, arrayContaining(equalTo(1),equalTo(2),equalTo(3)));
	    assertThat(new String[]{"ab","cd","ef"},arrayContaining(containsString("a"),containsString("c"),containsString("e")));
	    //忽略元素顺序
	    assertThat(new Integer[]{1,2,3},arrayContainingInAnyOrder(1,3,2));
	    //比较数组大小
	    assertThat(new Integer[]{1,2,3},arrayWithSize(3));
	    //空数组
	    assertThat(new Integer[]{}, emptyArray());
	}
	
	@Test
	public void test2(){
		//closeTo表示在1.03和0.97范围之间都是对的
		assertThat(1.02, is(closeTo(1.0, 0.03)));
		assertThat(1, comparesEqualTo(1));
		assertThat(2, greaterThan(1));
		assertThat(1, greaterThanOrEqualTo(1));
		assertThat(1, lessThan(2));
		assertThat(1, lessThanOrEqualTo(1));
	}
	
	@Test
	public void test3(){
		assertThat(Arrays.asList("foo", "bar"), hasSize(2));
		assertThat(new ArrayList<String>(), is(empty()));
		assertThat(new ArrayList<String>(), is(emptyCollectionOf(String.class)));
		assertThat(new ArrayList<String>(), is(emptyIterable()));
		assertThat(new ArrayList<String>(), is(emptyIterableOf(String.class)));
		assertThat(Arrays.asList("foo", "bar"), contains("foo", "bar"));
		assertThat(Arrays.asList("foo", "bar"), containsInAnyOrder("bar", "foo"));
		assertThat(Arrays.asList("foo", "bar"), iterableWithSize(2));
		assertThat(Arrays.asList("bar", "baz"), everyItem(startsWith("ba")));
		assertThat("foo", isIn(Arrays.asList("bar", "foo")));
		
		assertThat(new String[] {"foo", "bar"}, hasItemInArray(startsWith("ba")));
	}
	
	@Test
	public void test4(){
		HashMap<String, String> myMap=new HashMap<>();
		myMap.put("bar", "foo");
	    assertThat(myMap, hasEntry("bar", "foo"));
	    assertThat(myMap, hasKey(equalTo("bar")));
	    assertThat(myMap, hasValue("foo"));
	}
	
	@Test
	public void test5(){
		assertThat("Foo", equalToIgnoringCase("FOO"));
		assertThat("   my\tfoo  bar ", equalToIgnoringWhiteSpace(" my  foo bar"));
		assertThat("", isEmptyString());
		assertThat("myfoobarbaz", stringContainsInOrder(Arrays.asList("foo", "bar")));
		assertThat(true, hasToString("true"));
	}
	
	@Test
	public void test6(){
		assertThat(Integer.class, typeCompatibleWith(Number.class));
	    
		Fibonacci myBean=new Fibonacci();
		assertThat(myBean, hasProperty("result"));
		
		myBean.setResult("abc");
		assertThat(myBean, hasProperty("result", equalTo("abc")));
		
		Fibonacci myBean2=new Fibonacci();
		myBean2.setResult("abc");
		assertThat(myBean, samePropertyValuesAs(myBean2));
		
	}
}
