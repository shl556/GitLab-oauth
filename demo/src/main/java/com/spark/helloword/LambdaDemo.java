package com.spark.helloword;
@FunctionalInterface
public interface LambdaDemo {
    //来自java.lang.Object的方法不计算在内
//	void run();
//    boolean equals(Object obj);
//    String toString();
	String getName(String firstName,String lastName);
	static void test(){
		 System.out.println("sun");
	}
	default void  test2(){
		System.out.println("默认实现");
	}
}
