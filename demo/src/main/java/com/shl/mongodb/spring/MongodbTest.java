package com.shl.mongodb.spring;

import static org.hamcrest.Matchers.sameInstance;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class MongodbTest {

	@Autowired
	private MongoTemplate mongoOps;

	private Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * spring mongodb要求所有需要被存储的实体类提供一个id字段或者用@id注解标注的字段，字段类型为String或者BigInteger
	 * 
	 * spring mongodb在处理java实体类到bson数据的映射转换时会将实体类中涉及的类层次比如内部类，引用的外部类等类名记录到
	 * 文档的底层字段_class中，方便执行类型转换。也可以通过@TypeAlias注解或者实现TypeInformationMapper接口，
	 * 在MappingMongoConverter实例化时注册该接口，从而 避免记录完整的类名到_class字段中
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	@Test
	public void crudTest() {
		Person p = new Person("Joe", 34);

		// 不指定具体的collection名称时默认根据类名自动创建一个collection，比如这里创建person collection
		// 内部通过doinsert方法执行插入时会返回一个ObjectId,并根据对象中是否已经设置ObjectId设置该返回的id
		mongoOps.insert(p);
		log.info("Insert: " + p);

		// Find
		p = mongoOps.findById(p.getId(), Person.class);
		log.info("Found: " + p);

		// Query类是查询操作的辅助类，可以设置查询条件，排序，skip，limit等方法,BasicQuery类继承自Query类，可以将bson格式的查询条件转化为Query对象,TextQuery继承自Query,扩展了文本匹配查询的方法，
		// NearQuery是地理位置查询时用于表示地理位置坐标匹配条件的类
		// CriteriaDefinition接口代表具体的查询的条件，有三个实现子类Criteria、TextCriteria、
		// GridFsCriteria。Criteria封装的基本的查询条件和地理坐标查询方面的查询条件
		// Update类实现了MongoDB中的常用更新操作操作符,update方法内部封装了set方法
		mongoOps.updateFirst(query(where("name").is("Joe")), update("age", 35), Person.class);
		p = mongoOps.findOne(query(where("name").is("Joe")), Person.class);
		log.info("Updated: " + p);

		p = mongoOps.findAndModify(query(where("name").is("Joe")), update("age", 36), Person.class);
		log.info("findAndModify: " + p);

		p = mongoOps.findAndModify(query(where("name").is("Joe")), update("age", 36),
				new FindAndModifyOptions().returnNew(true), Person.class);
		log.info("findAndModify，returnnew： " + p);

		// upsert表示更新时如果找不到符合指定条件的文档，就将查询条件和修改内容合并成一个新的文档并插入，FindAndModifyOptions合并了插入，修改和删除三种操作，默认为修改操作，returnnew表示把修改后的
		// 文档返回.upsert模式下无返回值
		p = mongoOps.findAndModify(query(where("name").is("Marry")), update("age", 24),
				new FindAndModifyOptions().upsert(true), Person.class);
		log.info("findAndModify， upinsert: " + p);

		// Delete
		mongoOps.remove(p);

		// Check that deletion worked
		List<Person> people = mongoOps.findAll(Person.class);
		log.info("Number of people = : " + people.size());

		mongoOps.dropCollection(Person.class);
	}

	@Test
	public void versionLockTest() {
		Person a = new Person("shl", 24);
		mongoOps.insert(a);
		Person b = mongoOps.findById(a.getId(), Person.class);

		a.setName("sun");
		mongoOps.save(a);

		// 抛出OptimisticLockingFailureException异常,实例b中的version值跟数据库中的version值不同
		// 使用Mongodb java Driver3是设置
		// WriteConcern为ACKNOWLEDGED，否则该异常会被吞掉，3.2这个版本不存在该问题
		mongoOps.save(b);
	}

	@Test
	public void geoTest() {

		/*
		 * mongodb spring 支持使用Geojson格式的数据，具体方法调用时使用geo包中的类即可
		 */
		Circle circle = new Circle(-73.99171, 40.738868, 0.01);
		List<Venue> venues = mongoOps.find(new Query(Criteria.where("location").within(circle)), Venue.class);

		Box box = new Box(new Point(-73.99756, 40.73083), new Point(-73.988135, 40.741404));
		List<Venue> venues2 = mongoOps.find(new Query(Criteria.where("location").within(box)), Venue.class);

		Point point = new Point(-73.99171, 40.738868);
		List<Venue> venues3 = mongoOps.find(new Query(Criteria.where("location").near(point).maxDistance(0.01)),
				Venue.class);

		Point location = new Point(-73.99171, 40.738868);
		NearQuery query = NearQuery.near(location).maxDistance(new Distance(10, Metrics.MILES));

		GeoResults<Venue> venues4 = mongoOps.geoNear(query, Venue.class);

	}

	@Test
	public void textIndexTest() {
		List<TextIndex> texts = new ArrayList<>();
		texts.add(new TextIndex("abc shl", "sun3"));
		texts.add(new TextIndex("abc shl2", "sun2"));
		texts.add(new TextIndex("abcsd", "sunrte"));
		texts.add(new TextIndex("abce", "shl3"));
		texts.add(new TextIndex("abce", "shl4"));
		texts.add(new TextIndex("abcf", "sunf"));
		texts.add(new TextIndex("abcgs", "joy5"));

		mongoOps.insertAll(texts);

		//使用文本查询时必须改查询的字段建立文本索引
		Query query = TextQuery.queryText(new TextCriteria().matchingAny("abce").notMatching("shl3")).sortByScore();
		List<TextIndex> pages = mongoOps.find(query, TextIndex.class);
		pages.forEach(s -> System.out.println(s));
		
		System.out.println("*****************");
		
		query = TextQuery.queryText(new TextCriteria().matchingPhrase("abc shl").notMatching("sun3")).sortByScore();
		pages = mongoOps.find(query, TextIndex.class);
		pages.forEach(s -> System.out.println(s));
        
		mongoOps.dropCollection(TextIndex.class);
		/*
查询结果似乎不怎么实用
 abce : shl4
abc shl : sun3
abc shl2 : sun2
*****************
abc shl2 : sun2
		 */
	}
	
	
}
