package com.shl.redis.spring;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

@Configuration
//读取配置文件
@PropertySource("classpath:jedisPoolConfig.properties")
//自动扫描
@ComponentScan("com.shl.redis.spring")
//引入其他配置方式中定义的bean，允许通过Autowired或者Injec注解注入其他已定义的bean
//@Import(Configure.class)
@ImportResource("classpath:applicationContext.xml")
public class Configure {
	@Autowired
	private Environment env;
	@Bean
	public JedisPoolConfig jedisPoolConfig(){
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxTotal(env.getProperty("redis.pool.maxActive", Integer.class));
		config.setMaxIdle(env.getProperty("redis.pool.maxIdle", Integer.class));
		config.setMaxWaitMillis(env.getProperty("redis.pool.maxWait", Long.class));
		config.setTestOnBorrow(env.getProperty("redis.pool.testOnBorrow", Boolean.class));
		config.setTestOnReturn(env.getProperty("redis.pool.testOnReturn", Boolean.class));
	    return config;
	}
	
	
	@Bean
	public RedisConnectionFactory jedisConnectionFactory() {
	  JedisConnectionFactory factory=new JedisConnectionFactory(jedisPoolConfig());
	  //配置哨兵
	  //JedisConnectionFactory factory= new JedisConnectionFactory(redisSentinelConfiguration());
	  factory.setPort(env.getProperty("redis.port", Integer.class));
	  factory.setHostName(env.getProperty("redis.ip", String.class));
	  factory.setTimeout(env.getProperty("redis.pool.timeout", Integer.class));
	  return factory;
	}
 
	@Bean
	public StringRedisTemplate redisTemplate(){
		//StringRedisTemplate是对以String为键的RedisTemplate的优化，减少Serializer的配置
		StringRedisTemplate template=new StringRedisTemplate(jedisConnectionFactory());
//		RedisTemplate<Serializable, Serializable> template=new RedisTemplate<>();
//		template.setConnectionFactory(jedisConnectionFactory());
	    return template;
	}
	
	//通过SharedJedisPool获取SharedJedis操作数据库
	@Bean
	public ShardedJedisPool shardedJedisPool(){
		List<JedisShardInfo> shards = Arrays.asList(  
       		 new JedisShardInfo("172.27.12.85", 6379),  
                new JedisShardInfo("172.27.12.85", 6380)   
               );  
 
       ShardedJedisPool pool = new ShardedJedisPool(jedisPoolConfig(), shards);  
	   return pool;
	}
	
	@Bean
	public RedisSentinelConfiguration redisSentinelConfiguration() {
	  RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration() .master("mymaster")
	  .sentinel("127.0.0.1", 26379) .sentinel("127.0.0.1", 26380);
	  return sentinelConfig;
	}
	
	@Bean
	MessageListenerAdapter messageListener() { 
	    return new MessageListenerAdapter(new RedisMessageSubscriber());
	}
	
	@Bean
	RedisMessageListenerContainer redisContainer() {
	    RedisMessageListenerContainer container 
	      = new RedisMessageListenerContainer(); 
	    container.setConnectionFactory(jedisConnectionFactory()); 
	    container.addMessageListener(messageListener(), topic()); 
	    return container; 
	}
	
	@Bean
	@Lazy(false)//表示不延迟初始化
	MessagePublisher redisPublisher() { 
	    return new RedisMessagePublisher(redisTemplate(), topic());
	}
	
	@Bean
	ChannelTopic topic() {
	    return new ChannelTopic("messageQueue");
	}
}
