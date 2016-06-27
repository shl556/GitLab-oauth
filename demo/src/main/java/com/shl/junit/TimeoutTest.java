package com.shl.junit;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class TimeoutTest {
	
	@Test(timeout=1000)
	public void testWithTimeout() {
		 try {
			TimeUnit.MILLISECONDS.sleep(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	//Timeout设置的超时时间会应用在所有的test方法上，也包含Before和After方法，会忽略@Test中timeout参数
	@Rule
    public Timeout globalTimeout = Timeout.millis(500); // 10 seconds max per method tested

    @Test
    public void testSleepForTooLong() throws Exception {
        TimeUnit.MILLISECONDS.sleep(600); 
    }

	
}
