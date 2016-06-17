package com.shl.redis.spring;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
}
