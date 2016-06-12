package com.shl.lambda;
@FunctionalInterface
public interface LmdInterface {
	void run();
	//以下两个方法都是Object对象中的方法的抽象方法
	String toString();
	boolean equals(Object object);
	default void say(){
		System.out.println("hello");
	}
}
