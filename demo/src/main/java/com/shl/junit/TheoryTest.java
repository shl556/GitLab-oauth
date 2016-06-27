package com.shl.junit;
import static org.hamcrest.Matchers.*; //指定接下来要使用的Matcher匹配符
import static org.junit.Assume.*; //指定需要使用假设assume*来辅助理论Theory
import static org.junit.Assert.*; //指定需要使用断言assert*来判断测试是否通过

import org.junit.experimental.theories.DataPoint;	//需要使用注释@DataPoint来指定数据集
import org.junit.experimental.theories.Theories; //接下来@RunWith要指定Theories.class 
import org.junit.experimental.theories.Theory; //注释@Theory指定理论的测试函数
import org.junit.runner.RunWith; //需要使用@RunWith指定接下来运行测试的类

import org.junit.Test;

//注意：必须得使用@RunWith指定Theories.class
@RunWith(Theories.class)
public class TheoryTest {

    //利用注释@DataPoint来指定一组数据集，这些数据集中的数据用来证明或反驳接下来定义的Theory理论，
    //testNames1和testNames2这两个理论Theory测试函数的参数都是String，所以Junit4.4会将这5个
    //@DataPoint定义的String进行两两组合，统统一一传入到testNames1和testNames2中，所以参数名year
    //和name是不起任何作用的，"2007"同样有机会会传给参数name，"Works"也同样有机会传给参数year
    @DataPoint public static String YEAR_2007 = "2007";
    @DataPoint public static String YEAR_2008 = "2008";
    @DataPoint public static String NAME1 = "developer";
    @DataPoint public static String NAME2 = "Works";
    @DataPoint public static String NAME3 = "developerWorks";

    //注意：使用@Theory来指定测试函数，而不是@Test
    @Theory 
    public void testNames1( String year, String name ) {
        assumeThat( year, is("2007") ); //year必须是"2007"，否则跳过该测试函数
        System.out.println( year + "-" + name );
        assertThat( year, is("2007") ); //这里的断言语句没有实际意义，这里举此例只是为了不中断测试
    }

    //注意：使用@Theory来指定测试函数，而不是@Test
    @Theory
    public void testNames2( String year, String name ) {
        assumeThat(year, is("2007")); //year必须是"2007"，否则跳过该测试函数
        //name必须既不是"2007"也不是"2008"，否则跳过该测试函数
        assumeThat(name, allOf( not(is("2007")), not(is("2008"))));
        System.out.println( year + "-" + name );
        assertThat( year, is("2007") ); //这里的断言语句没有实际意义，这里举此例只是为了不中断测试
    }
}
