package com.h3c.Util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigManagerTest {

	@Test
	public void testGetClient_filename() {
		assertEquals("fdfs_client.conf", ConfigManager.getClient_filename());
		assertEquals(20971520, ConfigManager.getFragmentSize());
		assertEquals(1000, ConfigManager.getMaxConcurrentNum());
		assertEquals(104857600, ConfigManager.getThreadFragmentSize());
	}

	
}
