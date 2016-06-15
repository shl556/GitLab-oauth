package com.shl.redis;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**主要用于读取Jedis连接池的配置文件
 * @author Administrator
 *
 */
public class RedisConfig {
    protected static Logger log = LoggerFactory.getLogger(RedisConfig.class);
    public static String REDIS_IP;
    public static int REDIS_PORT;
    public static String REDIS_PASSWORD;
    public static int MAX_ACTIVE;
    public static int MAX_IDLE;
    public static long MAX_WAIT;
    public static boolean TEST_ON_BORROW;
    public static boolean TEST_ON_RETURN;
    public static int TIME_OUT;
    public static int RETRY_NUM;
    static {
        init();
    }
    public static void init() {
        try {
        	//默认从类所在的包下查找文件，加/表示从根路径下加载
        	InputStream in=RedisConfig.class.getResourceAsStream("/jedisPoolConfig.properties");
            if(in!=null){
                log.info("loading redis config from redis.properties.......");
                Properties p = new Properties();
                p.load(in);
                REDIS_IP = p.getProperty("redis.ip");
                REDIS_PORT = Integer.parseInt(p.getProperty("redis.port"));
                REDIS_PASSWORD = p.getProperty("redis.password");
                MAX_ACTIVE = Integer.parseInt(p.getProperty("redis.pool.maxActive"));
                MAX_IDLE = Integer.parseInt(p.getProperty("redis.pool.maxIdle"));
                MAX_WAIT = Integer.parseInt(p.getProperty("redis.pool.maxWait"));
                TEST_ON_BORROW = Boolean.parseBoolean(p.getProperty("redis.pool.testOnBorrow"));
                TEST_ON_RETURN = Boolean.parseBoolean(p.getProperty("redis.pool.testOnReturn"));
                TIME_OUT=Integer.parseInt(p.getProperty("redis.pool.timeout"));
                RETRY_NUM=Integer.parseInt(p.getProperty("redis.pool.retrynum"));
                log.info("redis config load Completed。");
                in.close();
                in=null;
            }else{
                log.error("jedisPoolConfig.properties is not found!");
                throw new RuntimeException("jedisPoolConfig.properties is not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static String getRedisIP() {
		return REDIS_IP;
	}
	public static int getRedisPort() {
		return REDIS_PORT;
	}
	public static String getRedisPassword() {
		return REDIS_PASSWORD;
	}
	public static int getMaxActive() {
		return MAX_ACTIVE;
	}
	public static int getMaxIdle() {
		return MAX_IDLE;
	}
	public static long getMaxWait() {
		return MAX_WAIT;
	}
	public static boolean isTestonBorrow() {
		return TEST_ON_BORROW;
	}
	public static boolean isTestonReturn() {
		return TEST_ON_RETURN;
	}
    public static int getTimeout(){
    	return TIME_OUT;
    }
    public static int getRetryNum() {
		return RETRY_NUM;
	}
}