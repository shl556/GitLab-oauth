package com.shl.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuleTest {
	  @Rule
	  //通过TemporaryFolder可以创建临时文件或者文件夹，由Temporary负责删除，但是不保证删除成功，而且删除失败的情况下不抛出异常
	  public TemporaryFolder tempFolder = new TemporaryFolder();

	  @Test
	  public void test() throws IOException {
		File folder = tempFolder.newFolder("test");
	    File file = tempFolder.newFile("test/test.txt");
        assertTrue(folder.isDirectory());
        assertTrue(file.isFile());
        System.out.println("准备删除文件");
	  }
	  
	  @Test
	  public void test2(){
		  System.out.println("已经删除文件");
		  File folder=new File("test");
		  File file=new File("test/test.txt");
		  assertFalse(folder.isDirectory());
	      assertFalse(file.isFile());
	  }

	  
	  @Rule
	  //跟try/catch配合使用，捕获异常使测试不会因为抛出异常而终止，并能在测试方法结束后抛出异常
	  public ErrorCollector collector = new ErrorCollector();

	  @Test
	  public void test3() {
		try{
		new ArrayList<>().get(2);
		}catch(Exception e){
	    collector.addError(e);
		}
	    System.out.println("抛出异常");
	    System.out.println("抛出异常后测试正常执行");
	  }
	 

	  
}
