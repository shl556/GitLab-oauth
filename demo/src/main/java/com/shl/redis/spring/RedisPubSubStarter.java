package com.shl.redis.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class RedisPubSubStarter  {
    public static void main(String[] args) {
    	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Configure.class);
//        ctx.refresh();
//        MessagePublisher publisher=ctx.getBean(MessagePublisher.class);
//        publisher.publish();
        ctx.registerShutdownHook();
    }
}
