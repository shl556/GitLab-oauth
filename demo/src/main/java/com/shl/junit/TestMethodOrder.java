package com.shl.junit;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/*
 * MethodSorters还有两个选项
 * DEFAULT选项是默认值，通过反射API返回的顺序执行test
 * JVM表示按照由java虚拟机生成的随机顺序决定
 * NAME_ASCENDING按照测试方法名称顺序
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMethodOrder {

    @Test
    public void testA() {
        System.out.println("first");
    }
    @Test
    public void testC() {
        System.out.println("second");
    }
    @Test
    public void testB() {
        System.out.println("third");
    }
}