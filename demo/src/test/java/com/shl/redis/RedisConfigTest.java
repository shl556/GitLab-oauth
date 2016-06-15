package com.shl.redis;

import static org.junit.Assert.*;

import org.junit.Test;

public class RedisConfigTest {

	@Test
	public void test() {
		assertEquals(RedisConfig.getMaxActive(), 1024);
		assertEquals(RedisConfig.getMaxIdle(), 200);
	}

}
