package com.shl.mongodb;


import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

public class MongoDBUtil {
	private static  MongoClient mongoClient;
	private static Map<String, Object> params;
	
	static {
		if (mongoClient == null) {
			MongoClientOptions.Builder build = new MongoClientOptions.Builder();
			build.codecRegistry(getCodecRegistry());
			build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
			build.socketKeepAlive(true);// 自动重连数据库启动
			build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
			/*
			 * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
			 * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
			 * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
			 */
			build.maxWaitTime(1000 * 60 * 2);
			build.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟

			MongoClientOptions myOptions = build.build();
			// 数据库连接池实例
			/*
			 * Document对象内部通过CodeC的实现类来完成bean各属性的转换，CodeC的实现类通过CodecProvider接口提供
			 * ，通过CodecRegistry对象的fromProviders方法完成CodecProvider接口实现类注册，具体调用时由CodecRegistry
			 * 的get方法负责从各CodecProvider对象中查找。
			 */
			mongoClient = new MongoClient("172.27.12.85", myOptions);
		}
	}
	
	private static CodecRegistry getCodecRegistry(){
    	CodecRegistry defaultCodecRegistry=MongoClient.getDefaultCodecRegistry();
    	return  CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(getCodecs()),defaultCodecRegistry);
    }
	
	private static Codec[] getCodecs(){
		return new Codec[]{new UserCodec()};
	}
	
	public static MongoClient getMongoClient(){
		return mongoClient;
	}
	
	public void close() {
		if(mongoClient!=null){
			mongoClient.close();
		}
	}
	
	public static  void put(String name ,Object value){
		if (params==null) {
			params=new HashMap<>();
		}
		params.put(name, value);
	}
	
	public static Map<String, Object> getMap(){
		Map<String, Object> result=new HashMap<>(params);
		params.clear();
		return result;
	}
}
