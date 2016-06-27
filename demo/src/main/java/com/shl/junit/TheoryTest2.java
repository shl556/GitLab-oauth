package com.shl.junit;
//指定需要使用断言assert*来判断测试是否通过
import static org.junit.Assert.assertTrue;

import static org.junit.Assume.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories; //接下来@RunWith要指定Theories.class 
import org.junit.experimental.theories.Theory; //注释@Theory指定理论的测试函数
import org.junit.runner.RunWith; //需要使用@RunWith指定接下来运行测试的类

//注意：必须得使用@RunWith指定Theories.class
@RunWith(Theories.class)
public class TheoryTest2 {
	  @DataPoints
	  public static int[] integers() {
	     return new int[]{
	                   1,2,3,4,5,-2,9,11,23,-4};
	  }
	 
	  //Theory会把数据集按照测试方法参数的个数和数据集数据个数进行排列组合，产生的数据组合会依次代入到测试方法中，当assume不通过时
	  //直接跳到下一个参数测试，当assert不通过时整个方法测试终止
	  //Theory可以理解为对参数化测试的扩展加强版，同一个参数集可以被多个测试方法使用
	  @Theory
	  public void a_plus_b_is_greater_than_a_and_greater_than_b(Integer a, Integer b) {
	     assumeTrue(a >4);
	     assertTrue(a + b > a);
	     assertTrue(a + b > b);
	     System.out.println("a="+a+",b="+b);
	  }
	 
	  @Theory
	  public void addition_is_commutative(Integer a, Integer b,Integer c) {
	     assumeTrue(a>3);
		  assertTrue(a + b-c == b + a - c);
	     System.out.println("a="+a+",b="+b+",c="+c);
	  }
}
