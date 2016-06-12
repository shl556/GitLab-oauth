package com.spark.helloword;


public  class LambdaDemoImp  implements LambdaDemo{

	@Override
	public String getName(String firstName, String lastName) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public void test2() {
    	// TODO Auto-generated method stub
//    	LambdaDemo.super.test2();
    	System.out.println("子类覆盖父类中的default方法");
    }
   public  static void test(){
		 System.out.println("子类中的同名静态方法");
	}
   public static void main(String[] args) {
	   //调用子类的同名静态方法  
	   LambdaDemoImp.test();
	   //调用父类中的同名静态方法
	     LambdaDemo.test();
	     LambdaDemoImp a=new LambdaDemoImp();
	     //调用子类中的覆写default方法
	     a.test2();
}
}
