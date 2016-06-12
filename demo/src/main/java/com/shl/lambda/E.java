package com.shl.lambda;

interface E extends A{
	@Override
	default void printA(){
	//printC()执行时会调用E中的printA方法，形成死循环
//	   printC();
	   A.super.printA();
	   System.out.println("接口间默认方法的继承");
	}
	//不允许覆写static方法
//	@Override
//	static void printC(){
//		System.out.println("覆写静态方法C");
//	}
	default void test() {
		// TODO Auto-generated method stub
		
	}
}

