package com.shl.lambda;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LmdTest {
	public void test2(){
	   Runnable run1=new Runnable() {
		@Override
		public void run() {
            System.out.println("匿名内部类");
		}
	};
	  Runnable run2=()->{ System.out.println("lambda表达式");
	  };
	  run1.run();
	  run2.run();
	}
	public void test3(){
		Comparator<Integer> com=(Integer x,Integer y)->{
			return x-y;
		};
		Comparator<Integer> com2=(x,y)->{
			return x-y;
		};
		Comparator<Integer> com3=(x,y)->x-y;
        //以下为java.util.function包中预定义的一些函数接口
		Supplier<Integer> obj=()->5;
		ToIntFunction<Integer> obj2=x->x*2;
		System.out.println(com.compare(1, 2));
		System.out.println(com2.compare(1, 2));
		System.out.println(com3.compare(1, 2));
		int i=obj.get();
		int j=obj2.applyAsInt(5);
		System.out.println(i+":"+j);
	}
	public void test4(){
		//静态方法引用
		Comparator<Integer> com=(x,y)->Integer.compare(x, y);
		Comparator<Integer> com2=Integer::compare;
		List<Person> persons=Arrays.asList(new Person(),new Person(),new Person());
		//forEach方法接受一个lambda表达式作为参数，对于list集合中每个元素都调用该表达式
		//实例方法引用
		persons.forEach(person->person.getFood());
		persons.forEach(Person::getFood);
		//父类方法引用
		persons.forEach(person->person.eat());
		persons.forEach(Human::eat);
        List<Integer> ages=Arrays.asList(1,2,3,4,5);
        //构造方法引用
        ages.forEach(x->new Integer(x));
        ages.forEach(Integer::new);
        //数组构造方法引用
        IntFunction<Integer[]> a=x->new Integer[x];
        IntFunction<Integer[]> a2=Integer[]::new;
        a.apply(10);
        a2.apply(10);
	}
	private int a=1;
	private static int b=2; 
	private int d=2;
	public void test5(int c){
	  int c2=1;
	  final int c3=1;
      IntFunction<Integer> f=x->x+a;
      IntFunction<Integer> f2=x->x+b;
      IntFunction<Integer> f3=x->{
    	 //lambda表达式中不能修改其引用的外部方法的局部变量，一旦被lambda表达式引用，该变量在整个方法体中
    	  //同样不能被修改，否则报错
    	  b++;
//    	  c2++;
//    	  c++;
    	  int d=2;
    	  //不能其被定义的外部方法中的局部变量相同的变量，但可以与类中成员变量同名
//    	  int c2=3;
//    	  int c=2;
    	  return b+x+c2;
      };
      IntFunction<Integer> f4=x->{
    	  a++;
    	  return a+x;
      };
//      c2=3;
	}
	public void test6(){
		Runnable run=()->{
			System.out.println(this);
		};
		Runnable run2=()->{
			System.out.println(toString()+getName());
		};
		run.run();
		run2.run();
	}
	@Override
	public String toString() {
		return "helloworld";
	}
	private String getName(){
		return "shl";
	}
	public void test7(){
		  List<Integer> list=Arrays.asList(1,2,3,4,5,2,3,4,6);
		  //将lambda表达式应用于迭代集合元素，所有的集合类都有forEach方法，接受一个lambda表示作为参数
		   list.forEach((Integer a)->{
			   System.out.println(a);
		   });
		   //将集合作为数据源按照流（管道模式）进行处理，具体而言是就是让集合中的元素依次通过各种过滤、转换等操作，
		   //注意整个过程中集合元素只迭代一次。
		  System.out.println("=========去除重复==========");
		  list.stream().distinct().forEach(System.out::print); 
		  System.out.println();
		  System.out.println("=========过滤==========");
		  list.stream().filter(s->{
			   System.out.println(s);
			   return s>3;
		   }).forEach(s->{
			   System.out.println(s);
		   });
		  System.out.println("=========元素转换==========");
		  list.stream().filter(i->i>4)
		                        .map(i->i*2)
		                        .forEach(s->System.out.print(s+" "));
		  //flatMap中Lambda表达式返回的是一个Stream对象
		  System.out.println("=========元素转换==========");
		  list.stream().filter(i->i>4)
		  .flatMap(i->Stream.of(i*2))
		  .forEach(s->System.out.print(s+" "));
		  System.out.println("=========调试打印==========");
		  list.stream().filter(i->i>4)
		  .peek(i->System.out.print(i+" "))
		  .flatMap(i->Stream.of(i*2))
		  .peek(i->System.out.print(i+" "))
		  .forEach(s->System.out.print(s+" "));
		  System.out.println("=========丢弃==========");
		 long count=  list.stream().filter(i->i>2)
		  .skip(1)        //丢弃前n个元素
		  .peek(i->System.out.println(i+" "))
		  .count();    //计算流中元素的数量
		 System.out.println("=========求和==========");
		 long sum=  list.stream().filter(i->i>2)
				 .peek(i->System.out.println(i+" "))
				 .mapToInt(i->i*2)  //转换成IntStream
				 .sum();    //计算流中元素的数量
		 System.out.println("=========将流转换成列表==========");
		 List<Integer> lists=  list.stream().filter(i->i>2)
				 .peek(i->System.out.println(i+" "))
				 .collect(Collectors.toList());    //计算流中元素的数量
		 System.out.println(lists.size());
		 System.out.println("=========通过reduce实现求和==========");
		int sum2=  list.stream().filter(i->i>2)
				 .peek(i->System.out.println(i+" "))
				 .reduce((s,item)->s+item)//第一个参数是上次执行的结果，第二个参数是流中的元素，第一次执行时用流中第一个元素和第二个元素相加
				 .get();
		System.out.println(sum2);
		   //parallelStream()返回一个并行的流，以利用CPU的多核特性
//		   list.parallelStream().filter(s->{
//			   System.out.println(s);
//			   return s>3;
//		   }).forEach(s->{
//			   System.out.println(s);
//		   });
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
	public void test8(){
		Stream<Integer> integerStream = Stream.of(1, 2, 3, 5);
		Stream<String> stringStream = Stream.of("taobao","aba","sun");
//必须添加limit()方法限制生成的stream流中元素的个数，否则会无限打印
		//		Stream.generate(() -> Math.random()).limit(10).forEach(System.out::println);;
		//iterate方法第一个参数是起始值或者说是种子，后面的lambda表达式是对流中元素的生成函数
		//0123456789
//		Stream.iterate(0, item -> item + 1).limit(10).forEach(System.out::print);
	    integerStream.limit(10).forEach(i->System.out.print(i+" "));
	}
	public void test9(){
		
	}
	public void test(){
		
	}
	public static void main(String[] args) {
		new LmdTest().test7();
	}
}
