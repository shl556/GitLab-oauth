package com.shl.redis.spring;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
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
//@ImportResource("classpath:applicationContext.xml")
//相当于<task:annotation-driven/>
//@EnableScheduling
//开启注解缓存支持，相当于<cache:annotation-driven />
@EnableCaching
public class Configure {
	@Autowired
	private Environment env;
	//从配置文件中读取数据
	@Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.cluster.timeout}")
    private Long timeout;
   @Value("${spring.redis.cluster.max-redirects}")
    private int redirects;
   
   
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
	  //配置集群，ClusterOperations clusterOps = redisTemplate.opsForCluster()操作数据库
//	  JedisConnectionFactory factory2=new JedisConnectionFactory(getClusterConfiguration(),jedisPoolConfig());
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
	
	@Bean
	//Redis的缓存管理器
	 CacheManager cacheManager() {
	     return new RedisCacheManager(redisTemplate());
	 }
	
    @Bean  //自定义缓存key生成器
    public KeyGenerator customKeyGenerator() {  
        return new KeyGenerator() {  
            @Override  
            public Object generate(Object o, Method method, Object... objects) {  
                StringBuilder sb = new StringBuilder();  
                sb.append(o.getClass().getName());  
                sb.append(method.getName());  
                for (Object obj : objects) {  
                    sb.append(obj.toString());  
                }  
                return sb.toString();  
            }  
        };  
    } 
    
    @Bean
    public RedisClusterConfiguration getClusterConfiguration() {
         Map<String, Object> source = new HashMap<String, Object>();
         //key值是固定的
         source.put("redis.cluster.nodes", clusterNodes);
         source.put("spring.redis.cluster.timeout", timeout);
         source.put("spring.redis.cluster.max-redirects", redirects);
         //MapPropertySource是对properties属性文件的一个抽象表示，但是他不能直接读取配置文件，只能通过map的方式手动构造
        return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));

       }
}
