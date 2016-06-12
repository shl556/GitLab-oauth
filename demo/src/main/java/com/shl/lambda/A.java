package com.shl.lambda;
interface A{
	//接口中可以定义变量，默认是public static final，必须初始化，可以加final，static，public修饰
	final int i=1;
	void test();
	default void printA(){
		System.out.println("A"+i);
	}
   default void printC(){
	   System.out.println("C");
	   //默认方法中可以访问静态方法或者默认方法
	   printB();
	   printA();
   }
   //default，static，abstract三个关键字中不能同时有两个关键字修饰一个方法
//   default static void printD(){
//	   System.out.println("C");
//   }

	static void printB(){
		//static方法中不能访问default方法，只能访问静态方法
//		printC()
		System.out.println("静态方法A");
	}
	static void printD(){
	    printB();
	}
}
