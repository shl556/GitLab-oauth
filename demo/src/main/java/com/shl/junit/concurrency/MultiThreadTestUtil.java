package com.shl.junit.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
public class MultiThreadTestUtil {
    
	/** 执行并发测试的工具类
	 * @param message 测试失败的提示信息
	 * @param runnables 运行测试的Runnable
	 * @param maxTimeoutSeconds 整个并发测试的超时时间
	 * @throws InterruptedException 中断测试
	 */
	public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
	    final int numThreads = runnables.size();
	    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
	    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
	    try {
	    	/*
	    	 * 用给定的计数 初始化 CountDownLatch,可以通过countDown()方法减小计数，在计数为0之前调用的await()方法的线程都会被阻塞，计数为0时所有调用之前调用或者之后调用await方法
	    	 * 的线程会立即执行并返回，注意计数不可以重置，即一个CountDownLatch实例只能用一次。CyclicBarrier与之的区别在于CyclicBarrier可以重复使用。
	    	 */
	        final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
	        final CountDownLatch afterInitBlocker = new CountDownLatch(1);
	        final CountDownLatch allDone = new CountDownLatch(numThreads);
	        for (final Runnable submittedTestRunnable : runnables) {
	            threadPool.submit(new Runnable() {
	                public void run() {
	                    allExecutorThreadsReady.countDown();
	                    try {
	                        afterInitBlocker.await();
	                        submittedTestRunnable.run();
	                    } catch (final Throwable e) {
	                        exceptions.add(e);
	                    } finally {
	                        allDone.countDown();
	                    }
	                }
	            });
	        }
	        //等待所有的线程完成初始化并设置超时时间，超时时间为线程数*10毫秒
	        assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
	        //同步运行所有的测试
	        afterInitBlocker.countDown();
	        assertTrue(message +" timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
	    } finally {
	        threadPool.shutdownNow();
	    }
	    assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
	}
}
