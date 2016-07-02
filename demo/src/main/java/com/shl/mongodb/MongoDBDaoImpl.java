package com.shl.mongodb;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class MongoDBDaoImpl {

	/**
	 * MongoClient的实例代表数据库连接池，是线程安全的，可以被多线程共享，整个应用只需维持一个实例即可
	 * 
	 * 构造方法中主要配置ip地址和端口号，连接池选项和权限认证信息，没有指定ip和端口时默认使用localhost和27017端口。主要参数类型：
	 * ServerAddress表示主机IP地址和端口号 MongoCredential表示连接到MongoDB的权限认证
	 * MongoClientOptions表示连接池和MongoDB常用属性设置的工具类，通过build()方法获取MongoClientOptions
	 * .Builder 对象，通过该对象可以连续设置各常用属性。
	 */
	private static MongoClient mongoClient = null;
	private Logger log = LoggerFactory.getLogger(getClass());

	static {
		if (mongoClient == null) {
			MongoClientOptions.Builder build = new MongoClientOptions.Builder();
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
			mongoClient = new MongoClient("172.27.12.85", myOptions);
		}
	}

	/******** 单例模式声明开始，采用饿汉式方式生成，保证线程安全 ********************/
	// 类初始化时，自行实例化，饿汉式单例模式
	private static final MongoDBDaoImpl mongoDBDaoImpl = new MongoDBDaoImpl();

	public static MongoDBDaoImpl getMongoDBDaoImplInstance() {
		return mongoDBDaoImpl;
	}

	public boolean delete(String dbName, String collectionName, Map<String, Object> keys) {
        MongoCollection dbCollection=getDBCollection(dbName, collectionName);
		validateMap(keys);
        BasicDBObject doc = new BasicDBObject(keys); // 构建删除条件
		DeleteResult result = dbCollection.deleteMany(doc); // 执行删除操作
		if (result!=null) {
			log.info("删除数据库{}的集合{}中符合条件{}的文档{}条",dbName,collectionName,keys,result.getDeletedCount());
		}
		return result.wasAcknowledged();
	}

	@Override
	public ArrayList<DBObject> find(String dbName, String collectionName, String[] keys, Object[] values, int num) {
		ArrayList<DBObject> resultList = new ArrayList<DBObject>(); // 创建返回的结果集
		DB db = null;
		DBCollection dbCollection = null;
		DBCursor cursor = null;
		if (keys != null && values != null) {
			if (keys.length != values.length) {
				return resultList; // 如果传来的查询参数对不对，直接返回空的结果集
			} else {
				try {
					db = mongoClient.getDB(dbName); // 获取数据库实例
					dbCollection = db.getCollection(collectionName); // 获取数据库中指定的collection集合

					BasicDBObject queryObj = new BasicDBObject(); // 构建查询条件

					for (int i = 0; i < keys.length; i++) { // 填充查询条件
						queryObj.put(keys[i], values[i]);
					}
					cursor = dbCollection.find(queryObj); // 查询获取数据
					int count = 0;
					if (num != -1) { // 判断是否是返回全部数据，num=-1返回查询全部数据，num!=-1则返回指定的num数据
						while (count < num && cursor.hasNext()) {
							resultList.add(cursor.next());
							count++;
						}
						return resultList;
					} else {
						while (cursor.hasNext()) {
							resultList.add(cursor.next());
						}
						return resultList;
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					if (null != cursor) {
						cursor.close();
					}
					if (null != db) {
						db.requestDone(); // 关闭数据库请求
					}
				}
			}
		}

		return resultList;
	}

	@Override
	public DBCollection getCollection(String dbName, String collectionName) {
		// TODO Auto-generated method stub
		return mongoClient.getDB(dbName).getCollection(collectionName);
	}

	@Override
	public DB getDb(String dbName) {
		// TODO Auto-generated method stub
		return mongoClient.getDB(dbName);
	}

	@Override
	public boolean inSert(String dbName, String collectionName, String[] keys, Object[] values) {
		DB db = null;
		DBCollection dbCollection = null;
		WriteResult result = null;
		String resultString = null;
		if (keys != null && values != null) {
			if (keys.length != values.length) {
				return false;
			} else {
				db = mongoClient.getDB(dbName); // 获取数据库实例
				dbCollection = db.getCollection(collectionName); // 获取数据库中指定的collection集合
				BasicDBObject insertObj = new BasicDBObject();
				for (int i = 0; i < keys.length; i++) { // 构建添加条件
					insertObj.put(keys[i], values[i]);
				}

				try {
					result = dbCollection.insert(insertObj);
					resultString = result.getError();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (null != db) {
						db.requestDone(); // 请求结束后关闭db
					}
				}
				return (resultString != null) ? false : true;
			}
		}
		return false;
	}

	@Override
	public boolean isExit(String dbName, String collectionName, String key, Object value) {
		// TODO Auto-generated method stub
		DB db = null;
		DBCollection dbCollection = null;
		if (key != null && value != null) {
			try {
				db = mongoClient.getDB(dbName); // 获取数据库实例
				dbCollection = db.getCollection(collectionName); // 获取数据库中指定的collection集合
				BasicDBObject obj = new BasicDBObject(); // 构建查询条件
				obj.put(key, value);

				if (dbCollection.count(obj) > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (null != db) {
					db.requestDone(); // 关闭db
					db = null;
				}
			}

		}
		return false;
	}

	@Override
	public boolean update(String dbName, String collectionName, DBObject oldValue, DBObject newValue) {
		DB db = null;
		DBCollection dbCollection = null;
		WriteResult result = null;
		String resultString = null;

		if (oldValue.equals(newValue)) {
			return true;
		} else {
			try {
				db = mongoClient.getDB(dbName); // 获取数据库实例
				dbCollection = db.getCollection(collectionName); // 获取数据库中指定的collection集合

				result = dbCollection.update(oldValue, newValue);
				resultString = result.getError();

				return (resultString != null) ? false : true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (null != db) {
					db.requestDone(); // 关闭db
					db = null;
				}
			}

		}

		return false;
	}

	private MongoCollection getDBCollection(String dbName,String collectionName){
	    if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(collectionName)){
	    	throw new RuntimeException("dbName 或者 collectionName不能为空");
	    }
	    if(mongoClient==null){
	    	throw new RuntimeException("mongoClient为空");
	    }
	    MongoCollection collection=mongoClient.getDatabase(dbName).getCollection(collectionName);
	    if (collection!=null) {
			log.info("获取数据库{}中的文档{}成功",dbName,collectionName);
		}
	    return collection;
	}
	
    private void validateMap(Map<String, Object> keys){
    	if(keys==null||keys.isEmpty()){
    		throw new RuntimeException("参数不能为空");
    	}
    }
    
   public static void main(String[] args) {
       
   }
	
}
