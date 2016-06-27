package com.shl.junit.theory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.experimental.theories.ParametersSuppliedBy;

@Retention(RetentionPolicy.RUNTIME)
//声明注解接口所使用的委托处理类
@ParametersSuppliedBy(BetweenSupplier.class)
public @interface Between{
 // 声明所有可用参数，效果为 @Between([first = int,] last = int)
 int first() default 0;  // 声明默认值，成为非必须提供项
 int last();
}