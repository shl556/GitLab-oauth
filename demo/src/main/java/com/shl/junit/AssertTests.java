package com.shl.junit;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
//提供对多种数据类型的重载和指定测试失败提示消息的重载
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

public class AssertTests {
  @Test
  public void testAssertArrayEquals() {
    byte[] expected = "trial".getBytes();
    byte[] actual = "trial".getBytes();
    assertArrayEquals("failure - byte arrays not same", expected, actual);
  }

  @Ignore("Test is ignored as a demonstration")
  @Test
  public void testAssertEquals() {
    assertEquals("failure - strings are not equal", "text", "text");
  }

  @Test
  public void testAssertFalse() {
    assertFalse("failure - should be false", false);
  }

  @Test
  public void testAssertNotNull() {
    assertNotNull("should not be null", new Object());
  }

  @Test
  public void testAssertNotSame() {
    assertNotSame("should not be same Object", new Object(), new Object());
  }

  @Test
  public void testAssertNull() {
    assertNull("should be null", null);
  }

  @Test
  public void testAssertSame() {
    Integer aNumber = Integer.valueOf(768);
    assertSame("should be same", aNumber, aNumber);
  }

  // JUnit Matchers assertThat
  @Test
  public void testAssertThatBothContainsString() {
	//both 和and 一般放在一起配合使用，表示同时满足两个条件，与之对应的是either和or,表示或者，即满足其中任何一个条件
	//针对string类型三个方法containsString，endsWith，startsWith
    assertThat("albumen", both(containsString("a")).and(containsString("b")));
    assertThat("albumen", both(endsWith("en")).and(startsWith("")));
    assertThat("albumen",  either(containsString("a")).or(startsWith("al")));
  }

  @Test
  public void testAssertThatHasItems() {
	//指定的Iterables类型的集合中是否包含指定的对象
	  //针对Iterable集合类型的两个方法hasItem，hasItems，everyItem
	assertThat(Arrays.asList("one", "two", "three"), hasItem("one"));
    assertThat(Arrays.asList("one", "two", "three"), hasItems("one", "three"));
//    assertThat(Arrays.asList("one", "two", "three"), hasItems(containsString("on2")));
    assertThat(Arrays.asList("one1", "two1", "three1"), everyItem(containsString("1")));
  }

  @Test
  public void testAssertThatEveryItemContainsString() {
    assertThat(Arrays.asList(new String[] { "fun", "ban", "net" }), everyItem(containsString("n")));
  }

  // Core Hamcrest Matchers with assertThat
  @Test
  public void testAssertThatHamcrestCoreMatchers() {
    //满足所有匹配条件
	 assertThat("good", allOf(equalTo("good"), startsWith("good")));
    //不满足allof中任何一个条件
	 assertThat("good", not(allOf(equalTo("bad"), equalTo("good"))));
    //满足任意匹配条件
    assertThat("good", anyOf(equalTo("bad"), equalTo("good")));
    //不能等于3和4
    assertThat(7, not(either(equalTo(3)).or(equalTo(4))));
    //sameInstance表示相同的对象引用，
    assertThat(new Object(), not(sameInstance(new Object())));
    Object obj=new Object();
    Object obj2=obj;
    assertThat(obj, sameInstance(obj2));
    String s="shl";
    assertThat(s, instanceOf(String.class));
    assertThat(s, isA(String.class));
    assertThat(s, is("shl"));
    assertThat(s, equalTo("shl"));
  }

  @Test
  public void testAssertTrue() {
    assertTrue("failure - should be true", true);
  }
}