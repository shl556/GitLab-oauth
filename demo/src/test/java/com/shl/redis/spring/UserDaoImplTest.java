package com.shl.redis.spring;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/applicationContext.xml")
@ContextConfiguration(classes=Configure.class)
public class UserDaoImplTest {
    
	@Autowired
	private UserDaoImpl userDao;
	
	@Test
	public void testSave() {
		User user=new User();
		user.setUid("1");
		user.setAddress("北京");
        userDao.save(user);
        User user2=userDao.read("1");
        assertEquals(user.getAddress(), user2.getAddress());
        userDao.delete("1");
        User user3=userDao.read("1");
        assertNull(user3);
	}
	
	@Test
	public void testTransaction(){
		List<Object> results=userDao.TransactionTest();
		assertEquals(5, results.size());
		assertEquals(4, ((Set<String>)results.get(4)).size());
	}

}
