package com.h3c.Util;

import static org.junit.Assert.*;

import org.csource.fastdfs.StorageClient1;
import org.junit.Test;

public class FastDFSUtillTest {

	@Test
	public void testGetStorageClient1() {
      StorageClient1 client1=FastDFSUtill.getStorageClient1();
      assertNotNull(client1);
	}

}
