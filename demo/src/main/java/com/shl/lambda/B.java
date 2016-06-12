package com.shl.lambda;

interface B{
	void test();
	default void printA(){
		System.out.println("同名方法B");
	}
	static void printB(){
		System.out.println("静态方法B");
	}
}
