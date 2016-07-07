package com.shl.mongodb;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.util.Arrays;
import java.util.List;

import javax.print.Doc;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class AdvancedTest {
	private static  MongoClient client;
	@BeforeClass
	public static void init(){
		client=MongoDBUtil.getMongoClient();
	}
	
	@AfterClass
    public static void release(){
    	client.close();
    }
	
	@Test
	public void projectionTest(){
	   //projections可以限制返回的字段
	   FindIterable<Document> results=getDBcollection().find(eq("age", 27)).projection(include("username","score")).limit(10);
	   MongoCursor<Document> user=results.iterator();
	   user.forEachRemaining(t->{System.out.println(t.getString("username")+" : "+t.getInteger("score"));});
	}
	
	@Test
    public void aggregationsTest(){
		//aggregate方法的参数为list列表类型的Bson对象，这些对象按照添加的顺序形成一个命令管道，所有的数据会依次通过这些管道节点被处理直至被过滤掉
		//group方法的第一个参数代表分组的字段，第二个参数为对分组数据的计算方法，使用Accumulators中的静态方法完成，sum方法的第一个参数为计算的字段，第二参数为计算方式,$age表示取age字段的值，不能在此基础上执行常规运算
      AggregateIterable<Document> result=getDBcollection().aggregate(Arrays.asList(match(and(gte("age", 22),lte("age", 25))),group("$age", sum("totalScore","$score" ))));
      MongoCursor<Document> doc=result.iterator();
      doc.forEachRemaining(t->System.out.println(t.getInteger("_id")+" : "+t.getInteger("totalScore")));
    }
	
	private MongoCollection<Document> getDBcollection(){
		return client.getDatabase("shl").getCollection("test");
	}
}
