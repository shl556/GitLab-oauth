package com.h3c.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

public class RandomAccessFileTest{
    @Test
	public void test(){
    	try {
			RandomAccessFile file=new RandomAccessFile("test.txt", "rw");
		    System.out.println(file.getFilePointer());
		    System.out.println(file.length());
			file.seek(file.length());
		    byte[] content="收到就好了实际发生了发发发达就好撒你还记得公司股份收到".getBytes();
		    System.out.println(content.length);
			file.write(content);
			System.out.println(file.length());
//			file.close();
			
//		    FileOutputStream out=new FileOutputStream("test.txt");
//		    out.write(content);
//		    out.flush();
//		    out.close();
		    
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
