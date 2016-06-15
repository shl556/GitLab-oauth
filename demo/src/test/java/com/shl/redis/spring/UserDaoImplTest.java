package com.shl.redis.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class UserDaoImplTest {
    
	@Autowired
	private UserDao userDao;
	
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

}
