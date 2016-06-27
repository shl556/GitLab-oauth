package com.shl.junit.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {

	private static final int THREAD_COUNT = 30;

	private static ExecutorService threadPool = Executors
			.newFixedThreadPool(THREAD_COUNT);

	private static Semaphore s = new Semaphore(10);

	public static void main(String[] args) {
		for (int i = 0; i < THREAD_COUNT; i++) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println(Thread.currentThread().getName()+"准备获取数据库连接");
						s.acquire();
						System.out.println(Thread.currentThread().getName()+"已经获取数据库连接");
						Thread.currentThread().sleep(5000);
						System.out.println(Thread.currentThread().getName()+"执行数据保存操作");
						s.release();
						System.out.println(Thread.currentThread().getName()+"释放数据库连接");
					} catch (InterruptedException e) {
					}
				}
			});
		}

		threadPool.shutdown();
	}
}