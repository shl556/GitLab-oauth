package com.shl.junit;
//指定需要使用断言assert*来判断测试是否通过
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.Theories; //接下来@RunWith要指定Theories.class 
import org.junit.experimental.theories.Theory; //注释@Theory指定理论的测试函数
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith; //需要使用@RunWith指定接下来运行测试的类

import com.shl.junit.theory.AllValueSupplier;
import com.shl.junit.theory.Between;

//注意：必须得使用@RunWith指定Theories.class
@RunWith(Theories.class)
public class TheoryTest3 {
	 @Theory
	 public final void test(@TestedOn(ints = { 0, 1, 2,4,5,3 }) int i,@TestedOn(ints = { 0, 1, 2,4,5,3 }) int j) {
	     Assume.assumeTrue(i>3); 
		 assertTrue(i+j>=i-j);
		 System.out.println("i="+i+",j="+j);
	 }
	 
	 @Theory
	 public final void test2(@Between(last = 0) int i, @Between(first = 3, last= 10) int j) {
	      // i 取值为 0（first默认=0，last=0），j 取值为 3-10
	      assertTrue(i + j >= 0);
	  }
	 
	 @Theory
	 public final void test(@ParametersSuppliedBy(AllValueSupplier.class) int i) {
	      // i 取值为 0-100
	      assertTrue(i >= 0);
	 }

	  
	 
}
