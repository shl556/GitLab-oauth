package com.shl.mongodb;

import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class BaseDaoImpl<T> {

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

	public BaseDaoImpl(){
		mongoClient=MongoDBUtil.getMongoClient();
	}
	
	
	public boolean delete(String dbName, String collectionName, Bson filter) {
        MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
		DeleteResult result = dbCollection.deleteMany(filter); // 执行删除操作
		if(result.getDeletedCount()>0){
			log.info("删除数据库{}的集合{}中符合条件{}的文档{}条",dbName,collectionName,filter,result.getDeletedCount());
		    return true;
		}else{
			log.info("删除失败或者待删除的数据不存在");
			return false;
		}
	}

	//通过Filters类中的静态方法设置查询条件
	public ArrayList<T> find(String dbName, String collectionName, Bson query,Class<T> resultClass) {
		 MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
	    //find方法中如果指定返回值类型就必须提供对应的Codec实现类，否则会因为无法转化bson数据到期望的java类而报错
		//如果不指定具体的java类则返回Document对象，即代表一段bson字符串的对象
		 FindIterable<T> iterable=dbCollection.find(query,resultClass);
	    MongoCursor<T> cursor=iterable.iterator();
	    ArrayList<T> results=new ArrayList<>();
	    cursor.forEachRemaining(t->{results.add(t);});
	    if (!results.isEmpty()) {
			log.info("查询数据库{}的集合{}中符合条件{}的文档{}条",dbName,collectionName,query,results.size());
		}
	    return results;
	}


	public boolean add(String dbName, String collectionName, Map<String, Object> keys) {
		 MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
	    validateMap(keys);
	    Document doc=new Document(keys);
	    //插入时只能使用键值对的形式插入，不能以一个对象的形式插入，UerCodec中的encode方法没有在这里调用
        //还有一个insertMany方法允许插入list列表形式的document对象。
	    dbCollection.insertOne(doc);
	    log.info("数据库{}的集合{}中新增文档{}成功",dbName,collectionName,keys);
	    return true;
	}

	public boolean isExit(String dbName, String collectionName, Bson query) {
		 MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
	    long sum=dbCollection.count(query);
	    if(sum>0){
	    	 log.info("数据库{}的集合{}中文档{}存在{}条",dbName,collectionName,query,sum);
	         return true;
	    }else{
	    	log.info("数据库{}的集合{}中文档{}不存在",dbName,collectionName,query);
	         return false;
	    }
	}

	public boolean update(String dbName, String collectionName, Bson filter,Bson update) {
		MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
	    UpdateResult result=dbCollection.updateMany(filter, update);
	    log.info("修改数据库{}的集合{}中符合条件{}的记录{}条",dbName,collectionName,filter,result.getModifiedCount());
	    //Mongodb会自动判断修改的内容与原内容，如果相同，即使执行修改操作，UpdateResult中getModifiedCount依然返回0
	    if (result.getModifiedCount()>0) {
			return true;
		}
	    return false;
	}

	private MongoCollection<Document> getDBCollection(String dbName,String collectionName){
	    if(StringUtils.isEmpty(dbName)||StringUtils.isEmpty(collectionName)){
	    	throw new RuntimeException("dbName 或者 collectionName不能为空");
	    }
	    if(mongoClient==null){
	    	throw new RuntimeException("mongoClient为空");
	    }
	    MongoCollection<Document> collection=mongoClient.getDatabase(dbName).getCollection(collectionName);
	    if (collection!=null) {
			log.info("获取数据库{}中的文档{}成功",dbName,collectionName);
		}
	    return collection;
	}
	
	public List<T> getByCondition(String dbName,String collectionName,Class<T> resultClass,Bson filter,PageInfo page){
		MongoCollection<Document> dbCollection=getDBCollection(dbName, collectionName);
		FindIterable<T> result=dbCollection.find(filter,resultClass).sort(descending("score","age")).skip(page.getStart()).limit(page.getPageNum());
		ArrayList<T> results=new ArrayList<>();
		MongoCursor<T> cursor=result.iterator();
		cursor.forEachRemaining(t->results.add(t));
		return results;
	}
	
    private void validateMap(Map<String, Object> keys){
    	if(keys==null||keys.isEmpty()){
    		throw new RuntimeException("参数不能为空");
    	}
    }
    
    public static  void  close() {
		if(mongoClient!=null){
			mongoClient.close();
		}
	}
    
    
}
