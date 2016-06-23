package com.h3c.DFSDao;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.h3c.DFSDemo.Config;
import com.h3c.Util.FileBroken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=Config.class)
public class FileBrokenDaoImplTest {
    @Autowired
	private FileBrokenDao dao;
    
	@Test
	public void testAdd() {
//		FileBroken broken=new FileBroken("shl7", "1.txt", 12, 30,new Date(), "连接超时");
//		dao.add(broken);
		FileBroken broken2=dao.get("1.txt");
		FileBroken broken3=dao.get("2.txt");
		assertEquals(12, broken2.getFileSize());
		assertEquals(30, broken2.getTargetFileSize());
		assertNull(broken3);
		FileBroken broken4=new FileBroken("shl7", "1.txt", 20, 30,new Date(), "连接超时");
		dao.update(broken4);
		broken2=dao.get("1.txt");
		assertEquals(20, broken2.getFileSize());
		dao.delete("shl7");
		broken2=dao.get("1.txt");
		assertNull(broken2);
	}

}
