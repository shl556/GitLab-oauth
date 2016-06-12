package com.spark.helloword;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public  class LambdaTest {
	private static  int temp=1;
	private int temp2=2;
	private final int temp3=3;
	public static void test2(){
		//runnable是一个只有一个方法的接口，编译器根据接口中的方法定义自动将其视为函数接口
		Runnable a=()->{
			for(int i=0;i<100;i++){
			System.out.println("sun");
			}
			};
		Thread thread=new Thread(a);
		thread.run();
		try {
			thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("休眠中止");
		thread.interrupt();
	}
	public static void test3(){
//		   LambdaDemo test=(String firstName, String  lastName)->{
//			    return firstName+lastName;
//		   };
		//lambda表达式本质上是对函数接口的实现的一种简写方式，其参数类型必须与
		//接口中的参数类型一致，也因此其参数类型可以不声明，编译器根据接口中方法的参数类型推断
		//lambda表达式返回的是一个实现了该函数接口的对象，调用该对象的函数接口方法即调用该函数接口。  
		LambdaDemo test=(firstName,lastName)->{
			   return firstName+lastName;
		   };
		   System.out.println(test.getName("sun", "hongliang"));
		   test.test2();
		   LambdaDemo.test();
	}
	public static void test4(){
		//使用jdk中的预定义函数接口
		 Predicate<String> predicate=(s)-> {
		       if(s.equals("sun")) return true;
		       return false;
		 };
		System.out.println(predicate.test("shl"));
		System.out.println(predicate.test("sun"));
	}
	public static void test5(){
		   List<Integer> list=Arrays.asList(1,2,3,4,5,6);
		  //将lambda表达式应用于迭代集合元素，所有的集合类都有forEach方法，接受一个lambda表示作为参数
//		   list.forEach((Integer a)->{
//			   System.out.println(a);
//		   });
		   //将集合作为数据源按照流（管道模式）进行处理，具体而言是就是让集合中的元素依次通过各种过滤、转换等操作，
		   //注意整个过程中集合元素只迭代一次。
		   list.stream().filter(s->{
			   System.out.println(s);
			   return s>3;
		   }).forEach(s->{
			   System.out.println(s);
		   });
		   /*
1
2
3
4
4
5
5
6
6
		    */
	}
	public static void test6(){
		Callable<Runnable> c1 = () ->{
		//内嵌的lambda表达式
			return 	()->{
				 System.out.println("Nested lambda");
			};
		};
		try {
			 Thread thread = new Thread(c1.call());
			 thread.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void test7(){
		 Callable<Integer> c2 = true ? (() -> 42) : (() -> 24);
		 try {
			System.out.println(c2.call());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void test8(int  temp4){
		int temp5=5;
		final int temp6=6;
		Function<Integer, Integer> function1=x->{ return x+temp;};
//		Function<Integer, Integer> function2=x-> x+temp2;
		//可以访问外围方法的方法参数或者局部变量，不要求是final
		Function<Integer, Integer> function2=x-> x+temp4;
		Function<Integer, Integer> function3=x-> x+temp5;
		Function<Integer, Integer> function4=x-> x+temp6;
		//不能再lambda表达式中修改方法类的局部变量，避免多线程对同一变量的修改引起的变量不一致问题
//		Function<Integer, Integer> function5=x-> {
//		    temp5+=x;
			//lambda表达式中不能定义与其所在的外部环境的变量的同名变量，即不允许覆盖掉外围环境的变量值
//		    int temp5=6;
//		    return temp5;
//		};
		//lambda表达式使用方法类局部变量或者方法参数时不要求必须添加final关键字，而是要求必须是effectively final
      //effectively final要求改变量再方法内不能被修改，否则编译报错
		//		temp5=9;
//		temp4=8;
		System.out.println(function1.apply(1));
		System.out.println(function2.apply(1));
		System.out.println(function3.apply(1));
		System.out.println(function4.apply(1));
	}
	public static void test9(){
		//generate()方法为生成器函数，用于给流提供数据源
		Stream.generate(()->Math.random()).limit(5).forEach(System.out::println);
		//Math::random是()->Math.random()的简写形式，称为方法引用
		Stream.generate(Math::random).limit(5).forEach(System.out::println);
	}
	public  void test10(){
		//lambda表达式中的this指代的是外围类对象
		 Runnable r=()->{System.out.println(this);};
		 Runnable r1=()->{System.out.println(toString());};
		 Runnable r2=()->{System.out.println(temp2);};
		 r2.run();
		 temp2=3;
		 Runnable r3=()->{System.out.println(temp2);};
		 Runnable r4=()->{
			 temp2=6;
			 System.out.println(temp2);};
		 r.run();
		 r1.run();
	     r3.run();
	     r4.run();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "词法作用域";
	}
	public static void test(){
	}
	public static void main(String[] args) {
		new LambdaTest().test10();
	}

}
