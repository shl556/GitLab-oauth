package com.shl.junit;


import org.junit.After;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.Statement;

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

	 @Rule
	 public RepeatableRule rule=new RepeatableRule(5, new String[]{"fails","succeeds"});
	 
	  @Test
	  public void fails() {
	      System.out.println("失败测试");
	      Assert.fail();
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
