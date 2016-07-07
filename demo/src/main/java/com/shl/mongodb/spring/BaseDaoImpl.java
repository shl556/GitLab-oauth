package com.shl.mongodb.spring;

import static com.mongodb.client.model.Filters.where;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class BaseDaoImpl<T> {

	@Autowired
	private MongoTemplate template;
	
//	private Logger log = LoggerFactory.getLogger(getClass());
//
//	public boolean delete(Query query,String collectionName) {
//        WriteResult result=template.remove(query, collectionName);
//	    if (result.isUpdateOfExisting()) {
//			log.info("删除成功");
//		}
//	}
//
//	//通过Filters类中的静态方法设置查询条件
//	public ArrayList<T> find(String dbName, String collectionName, Bson query,Class<T> resultClass) {
//		
//	}
//
//
//	public boolean add(String dbName, String collectionName, Map<String, Object> keys) {
//		 
//	}
//
//	public boolean isExit(String dbName, String collectionName, Bson query) {
//		
//	}
//
//	public boolean update(String dbName, String collectionName, Bson filter,Bson update) {
//		
//	}
//
//	
//	public List<T> getByCondition(String dbName,String collectionName,Class<T> resultClass,Bson filter,PageInfo page){
//		
//	}
	
   
    
    
}
