package com.shl.redis.spring;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;

public class RedisMessagePublisher implements MessagePublisher{
	 
    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ChannelTopic topic;
 
    public RedisMessagePublisher() {
    }
 
    public RedisMessagePublisher(
      StringRedisTemplate redisTemplate, ChannelTopic topic) {
      this.redisTemplate = redisTemplate;
      this.topic = topic;
    }
    
    @Scheduled( fixedDelay = 100 )
    public void publish() {
        redisTemplate.convertAndSend(topic.getTopic(), "message"+UUID.randomUUID());
    }
}