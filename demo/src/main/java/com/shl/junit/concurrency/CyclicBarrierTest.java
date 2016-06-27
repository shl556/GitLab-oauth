package com.shl.junit.concurrency;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierTest {

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		//设置触发点，即必须有3个线程调用await方法才能触发
		final CyclicBarrier cb = new CyclicBarrier(3);
		for (int i = 0; i < 5; i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						Thread.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() + "即将到达集合地点1，当前已有"
								+ cb.getNumberWaiting() + "个已经到达，正在等候");
						cb.await();// 到此如果没有达到公共屏障点，则该线程处于等待状态，如果达到公共屏障点则所有处于等待的线程都继续往下运行
                        System.out.println("执行任务1");
						
						Thread.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() + "即将到达集合地点2，当前已有"
								+ cb.getNumberWaiting() + "个已经到达，正在等候");
						cb.await();
						System.out.println("执行任务2");
						
						Thread.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() + "即将到达集合地点3，当前已有"
								+ cb.getNumberWaiting() + "个已经到达，正在等候");
						cb.await();
						System.out.println("执行任务3");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			service.execute(runnable);
		}
		service.shutdown();
	}
}