package com.h3c.DFSDao;

import static org.junit.Assert.*;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.DownloadCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.h3c.DFSDemo.Config;
import com.h3c.Util.DownloadFileWriter;
import com.h3c.Util.NameValuePairUtil;
import com.h3c.Util.StorageInfo;
import com.h3c.Util.UploadLocalFileSender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class DFSDaoImplTest {
	@Autowired
	private DFSDao dao;

	@Test
	public void testUploadFileNameValuePairArrayString() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";

		StorageInfo info = dao.uploadFile(fileInfo, filePath);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		boolean success = dao.download(info, downloadFilePath);
		assertTrue(success);

		info.setGroupName("shl");
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.download(info, downloadFilePath);
		assertFalse(success2);
	}

	@Test
	public void testUploadFileNameValuePairArrayStringString() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";
		String groupName = "group1";

		StorageInfo info = dao.uploadFile(fileInfo, filePath, groupName);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		DownloadCallback callback=new DownloadFileWriter(downloadFilePath);
		boolean success = dao.downloadByCallBack(info, downloadFilePath,callback);
		assertTrue(success);

		info.setGroupName("shl");
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.downloadByCallBack(info, downloadFilePath,callback);
		assertFalse(success2);
	}

	@Test
	public void testUploadFileByCallBack() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";
		String groupName = "group1";
        UploadLocalFileSender callback2=new UploadLocalFileSender(filePath);
		
		StorageInfo info = dao.uploadFileByCallBack(fileInfo, filePath, groupName,callback2);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		DownloadCallback callback=new DownloadFileWriter(downloadFilePath);
		boolean success = dao.downloadByCallBack(info, downloadFilePath,callback);
		assertTrue(success);

		dao.delete(info);
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.downloadByCallBack(info, downloadFilePath,callback);
		assertFalse(success2);
	}

	@Test
	public void testUploadSlaveFile() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";
		String groupName = "group1";

UploadLocalFileSender callback2=new UploadLocalFileSender(filePath);
		
		StorageInfo info = dao.uploadFileByCallBack(fileInfo, filePath, groupName,callback2);
		System.out.println(info);
		assertNotNull(info);

		String slavaFilePath = "appendFileId2.txt";
        String prefix="shltest";
		StorageInfo info2 = dao.uploadSlaveFile(fileInfo, slavaFilePath, info, prefix);
		System.out.println(info2);
		assertNotNull(info2);
		
		String downloadFilePath = "download/appendFileId2.txt";
		DownloadCallback callback=new DownloadFileWriter(downloadFilePath);
		boolean success = dao.downloadByCallBack(info2, downloadFilePath,callback);
		assertTrue(success);

	}

	@Test
	public void testUploadAppenderFileNameValuePairArrayString() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";

		StorageInfo info = dao.uploadAppenderFile(fileInfo, filePath);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		boolean success = dao.download(info, downloadFilePath);
		assertTrue(success);

		info.setGroupName("shl");
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.download(info, downloadFilePath);
		assertFalse(success2);
	}

	@Test
	public void testUploadAppenderFileNameValuePairArrayStringString() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";
        String groupName="group1";
		
		StorageInfo info = dao.uploadAppenderFile(fileInfo, filePath, groupName);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		boolean success = dao.download(info, downloadFilePath);
		assertTrue(success);

		info.setGroupName("shl");
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.download(info, downloadFilePath);
		assertFalse(success2);
		
//		groupName="group2";
//		
//		StorageInfo info3 = dao.uploadAppenderFile(fileInfo, filePath, groupName);
//		System.out.println(info3);
//		assertNull(info3);

	}

	@Test
	public void testUploadAppenderFileByCallBack() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "appendFileId.txt";
        String groupName="group1";
        UploadLocalFileSender callback2=new UploadLocalFileSender(filePath);
		
		StorageInfo info = dao.uploadFileByCallBack(fileInfo, filePath, groupName,callback2);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/appendFileId.txt";
		boolean success = dao.download(info, downloadFilePath);
		assertTrue(success);

		info.setGroupName("shl");
		String downloadFilePath2 = "download/appendFileId2.txt";
		boolean success2 = dao.download(info, downloadFilePath);
		assertFalse(success2);
	}

	@Test
	public void testUploadFileByFragment() {
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "jdk82.exe";
		
		StorageInfo info = dao.uploadFileByFragment(fileInfo, filePath);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/jdk82.exe";
		boolean success = dao.downloadByFragment(info, downloadFilePath);
		assertTrue(success);
	}

	@Test
    public void testdownloadByMultiThreadTest(){
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "jdk82.exe";
		
		StorageInfo info = dao.uploadFileByFragment(fileInfo, filePath);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/jdk82.exe";
		boolean success = dao.downloadByMultiThread(info, downloadFilePath);
		assertTrue(success);
    }
	
	@Test
    public void testuploadByMultiThreadTest(){
		NameValuePairUtil pairs = new NameValuePairUtil();
		pairs.add("owner", "shl");
		pairs.add("lastModifyTime", "2012-9-18");
		pairs.add("fileName", "appendFileId.txt");
		pairs.add("fileSize", "121212");
		NameValuePair[] fileInfo = pairs.getNameValuePairArray();

		String filePath = "jdk82.exe";
		
		StorageInfo info = dao.uploadFileByMultiThread(fileInfo, filePath);
		System.out.println(info);
		assertNotNull(info);

		String downloadFilePath = "download/jdk82.exe";
		boolean success = dao.downloadByMultiThread(info, downloadFilePath);
		assertTrue(success);
    }
	
}
