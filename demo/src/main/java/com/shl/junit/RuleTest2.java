package com.shl.junit;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuleTest2 {
	 private static String watchedLog;

//	  @Rule
//	  public TestRule watchman = new TestWatcher() {
//	    @Override
//	    public Statement apply(Statement base, Description description) {
//	      return super.apply(base, description );
//	    }
//
//	    @Override
//	    protected void succeeded(Description description) {
//	      watchedLog += description.getDisplayName() + " " + "success!\n";
//	    }
//
//	    @Override
//	    protected void failed(Throwable e, Description description) {
//	      watchedLog += description.getDisplayName() + " " + e.getClass().getSimpleName() + "\n";
//	    }
//
//	    @Override
//	    protected void skipped(AssumptionViolatedException e, Description description) {
//	      watchedLog += description.getDisplayName() + " " + e.getClass().getSimpleName() + "\n";
//	    }
//
//	    @Override
//	    protected void starting(Description description) {
//	      super.starting(description);
//	    }
//
//	    @Override
//	    protected void finished(Description description) {
//	      super.finished(description);
//	    }
//	  };
	 @BeforeClass
	 public static void BeforeClass(){
		 System.out.println("beforeClass方法2");
	 }
	 @AfterClass
	 public static void AfterClass(){
		 System.out.println("afterClass 方法2");
	 }
	 
	 
	 @Before
	 public void init (){
		 System.out.println("before方法2");
	 }
	 
	 @After
	 public void After(){
		 System.out.println("after方法2");
	 }

	 @Rule
	 public RepeatableRule2 rule=new RepeatableRule2();
//	 @ClassRule
//	 public static RepeatableRule2 rule=new RepeatableRule2();
//	 public RepeatableRule rule=new RepeatableRule(5, new String[]{"succeeds"});
	 
	  @Test
	  public void fails() {
	      System.out.println("失败测试");
	  }

	  @Test
	  public void succeeds() {
		  System.out.println("成功测试");
		  
	  }
	  
	  @Test
	  public void test(){
		  System.out.println("执行测试");
	  }
	  
//	  @After
//	  public void printLog(){
//		  System.out.println(watchedLog);
//	  }
	  
	  
}
