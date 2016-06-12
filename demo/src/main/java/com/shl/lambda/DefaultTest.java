package com.shl.lambda;

class D extends DefaultTest implements A,B,C{
	
}
class D2 implements A,E{

	@Override
	public void test() {
        System.out.println("D2");		
	}
	
}
class F implements A,C{

	@Override
	public void printA() {
		A.super.printA();
         System.out.println("默认方法不太代替接口实现");		
	}

	@Override
	public void test() {
		System.out.println("test");
	}
	
}
public class DefaultTest implements A,B{

	@Override
	public void test() {
	   A.super.printA();
       System.out.println("test");		
	}
	@Override
	public void printA(){
		//子类调用接口中的非静态方法
		A.super.printA();
	}
//	@Override,接口中同名的静态方法不存在冲突，子类也不能覆写
	static void printB(){
		//通过接口名直接调用接口中静态方法
		A.printB();
		B.printB();
	}
	public static void main(String[] args) {
	   D2 d2=new D2();
	   d2.printA();
	   d2.printC();
	}
}
