package com.shl.redis.spring;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao{
   
	@Autowired
//    protected RedisTemplate<Serializable, Serializable> redisTemplate;
	protected StringRedisTemplate redisTemplate;
	
	public void save(User user) {  
	    redisTemplate.execute(new RedisCallback<Object>() {  
	        public Object doInRedis(RedisConnection connection)  
	                throws DataAccessException {  
	            //针对RedisTemplate的RedisConnection，必须执行serialize操作将待处理的数据转化成byte数组
//	        	connection.set(  
//	                    redisTemplate.getStringSerializer().serialize(  
//	                            "user.uid." + user.getUid()),  
//	                    redisTemplate.getStringSerializer().serialize(  
//	                            user.getAddress())); 
	        	((StringRedisConnection)connection).set("user.uid."+user.getUid(),user.getAddress());
	            return null;  
	        }  
	    }); 
	    
	}  
    
	@Cacheable
	//默认情况下根据方法的参数生成key值，第二次调用时会根据方法参数生成的key值去数据库中查找是否存在相同的key，若有则取出，没有按照生成的key重新插入，主要用户缓存获取数据
	//可以指定key，指定key的生成器等
	//value属性表示该缓存的别名如Cacheable("users")
	//key属性表示用来生成key的SPEL表达式，默认使用的是参数名+参数值生成key
	//condition属性表示执行缓存的条件，如@Cacheable(value="messageCache", condition="'Joshua'.equals(#name)")表示当方法参数name的值为Joshua时才缓存结果
	//keyGenerator表示键生成器
	//@CacheEvict用于从缓存中移除key值，@CachePut用户往缓存中新增或者更新数据，@CacheConfig用于统一类级别的缓存配置
    public User read( String uid) {  
//        return redisTemplate.execute(new RedisCallback<User>() {  
//            public User doInRedis(RedisConnection connection)  
//                    throws DataAccessException {  
//                byte[] key = redisTemplate.getStringSerializer().serialize(  
//                        "user.uid." + uid);  
//                if (connection.exists(key)) {  
//                    byte[] value = connection.get(key);  
//                    String address = redisTemplate.getStringSerializer()  
//                            .deserialize(value);  
//                    User user = new User();  
//                    user.setAddress(address);  
//                    user.setUid(uid);  
//                    return user;  
//                }  
//                return null;  
//            }  
//        });
        String address=redisTemplate.opsForValue().get("user.uid."+uid);
        User user=null;
        if(address!=null){
        user=new User();
        user.setAddress(address);
        user.setUid(uid);
        }
        return user;
    }  

    public void delete(String uid) {  
//        redisTemplate.execute(new RedisCallback<Object>() {  
//            public Object doInRedis(RedisConnection connection) {  
//                connection.del(redisTemplate.getStringSerializer().serialize(  
//                        "user.uid." + uid));  
//                return null;  
//            }  
//        });
    	redisTemplate.delete("user.uid."+uid);
    }  

    //redisTemplate提供事务操作方法不保证同一个事务多个操作都得到执行，可以通过SessionCallback接口解决该问题
    //1.1以前spring会将各操作执行结果直接返回，但是不同操作返回的类型不一样，spring从1.1开始就放弃了这种做法，会对结果做适当的转换
    //可以通过connctionFactory设置convertPipelineAndTxResults为false来阻止结果转换
    //也可以对redisTemplate设置setEnableTransactionSupport为true，并借助Transational注解简化事务操作
    public List<Object> TransactionTest(){
    	List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
    		  public List<Object> execute(RedisOperations operations) throws DataAccessException {
    		    operations.multi();
    		    SetOperations<String, String> setOperations=operations.opsForSet();
    		    setOperations.add("key", "value1");
    		    setOperations.add("key", "value2");
    		    setOperations.add("key", "value3");
    		    setOperations.add("key", "value4");
                setOperations.members("key");
    		    // This will contain the results of all ops in the transaction
    		    return operations.exec();
    		  }
    		});
    	txResults.forEach(s->System.out.println(s+" / "));
    	return txResults;
    }
    
    //分奇怪的是executePipelined方法有返回值，但是Callback实现类只能返回一个空值，否则报错
    public List<Object> PipelineTest(){
//    	List<Object> results = redisTemplate.executePipelined(
//    			new SessionCallback<List<Object>>() {
//					@Override
//					public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
//	    			    ValueOperations<String, String> valueOperations=(ValueOperations<String, String>) operations.opsForValue();  
//						List<Object> results=new ArrayList<>();
//	    			      for(int i=0; i< 100; i++) {
//	    			        valueOperations.set("name"+i, "shl"+i);
//	    			      }
//					}
//    				
//				});
    	List<Object> results = redisTemplate.executePipelined(
    			  new RedisCallback<Object>() {
    			    public Object doInRedis(RedisConnection connection) throws DataAccessException {
    			      StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
    			      for(int i=0; i< 100; i++) {
    			        stringRedisConn.rPop("myqueue");
    			      }
    			    return null;
    			  }
    			});
    	return results;
    }
    
    
}
