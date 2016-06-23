package com.h3c.Util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class MD5UtilTest {

	@Test
	public void test() {
		//同一文件不同文件名生成的MD5应该是相同的
		String md5String=MD5Util.getFileMD5String("appendFileId.txt");
		String md5String2=MD5Util.getFileMD5String("download/appendFileId.txt");
		String md5String3=MD5Util.getFileMD5String("appendFileId3.txt");
	    assertTrue(MD5Util.checkPassword(md5String, md5String2));
	    assertTrue(MD5Util.checkPassword(md5String, md5String3));
	}

}
