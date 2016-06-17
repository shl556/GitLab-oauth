package com.shl.redis;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Test;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
  
  
  
public class JedisDemo  {  
    private static  Consumer<String> print=s->System.out.print(" "+s+" ");
    /** 
     * 在不同的线程中使用相同的Jedis实例会发生奇怪的错误。但是创建太多的实现也不好因为这意味着会建立很多sokcet连接， 
     * 也会导致奇怪的错误发生。单一Jedis实例不是线程安全的。为了避免这些问题，可以使用JedisPool, 
     * JedisPool是一个线程安全的网络连接池。可以用JedisPool创建一些可靠Jedis实例，可以从池中拿到Jedis的实例。 
     * 这种方式可以解决那些问题并且会实现高效的性能 
     */  
  
    public static void main(String[] args) {  
//        StringKeyTest();
//    	HashsetKeyTest();
//    	ListKeyTest();
//    	SetKeyTest();
//    	SortedSetKeyTest();
//        sortTest();
//    	new JedisDemo().SortTest2();
//    	new JedisDemo().PipelineTest();
    	new JedisDemo().CluserTest();
    	
    	JedisUtil.getPool().destroy();  
  
    }  
  
    public static void StringKeyTest() {  
        Jedis jedis = JedisUtil.getJedis();  
        try {  
            // 向key-->name中放入了value-->minxr  
            jedis.set("name", "minxr");  
            String ss = jedis.get("name");  
            System.out.println("1 "+ss);  
            
            //判断key是否存在
            System.out.println("2 "+jedis.exists("name"));
            
            //判断key值类型,返回值包括none（key不存在），string，list,set
            System.out.println("3 "+jedis.type("name"));
            
            jedis.set("name2", "shl2");
            jedis.set("name3", "shl3");
            jedis.set("name4", "shl4");
            //返回匹配指定模式的所有key值，以set形式返回，操作比较耗时影响性能，不推荐正常使用
            jedis.keys("name*").forEach(s->System.out.print(" "+s+" "));
            
            //新名与旧名相同报错，若旧名存在，则旧名对应的key值被重写，返回Ok或者fail
            System.out.println("5 "+jedis.rename("name2", "name3"));
            
            //与rename的区别在于若旧名存在则失败，且renamex返回int，成功返回1，失败返回0
            System.out.println("6 "+jedis.renamenx("name3", "name4"));
            
            //设定key的有效时间，单位为秒，与之相反的是persist，能够将具有有效时间的key转化成正常的没有有效期的key
            jedis.expire("name4", 3);
            Thread.sleep(1000);
            //查看剩余有效时间,单位是秒
            System.out.println("7 "+jedis.ttl("name4"));
            Thread.sleep(2100);
            //超时后自动被清除
            System.out.println("8 "+jedis.get("name4"));
            
            //将key移动到指定编号的数据库
            jedis.move("name3", 1);
            //选择对应编号的数据库
            jedis.select(1);
            printByIndex(jedis.get("name3"),9);
            
            // 很直观，类似map 将jintao append到已经有的value之后  
            jedis.select(0);
            jedis.append("name", "jintao");  
            printByIndex(jedis.get("name"),10);  
            
  
            // 删除key对应的记录  
            jedis.del("name");  
            printByIndex(jedis.get("name"),11);// 执行结果：null  
  
            /** 
             * mset相当于 jedis.set("name","minxr"); jedis.set("jarorwar","aaa"); 
             */  
            jedis.mset("name", "minxr", "jarorwar", "aaa");  
            System.out.println(jedis.mget("name", "jarorwar"));  
            
            //对name设置新的值shl并返回原name的值
            printByIndex(jedis.getSet("name", "shl"),12);
            
            //若key值不是可加减的整数则报错
//            jedis.incr("name");
//            printByIndex(jedis.get("name"), 13);
            //num不存在，将其key值初始化为1
            jedis.incr("num");
            printByIndex(jedis.get("num"), 13);
            //将num的key值加1
            jedis.incr("num");
            printByIndex(jedis.get("num"), 13);
            //将num的key值加20
            jedis.incrBy("num", 20);
            printByIndex(jedis.get("num"), 13);
            jedis.decrBy("num",10);            
            printByIndex(jedis.get("num"), 13);
            
            jedis.set("name","sunhongliang");
            //截取指定key值指定长度范围的string
            printByIndex(jedis.substr("name", 1, 3),14);
            printByIndex(jedis.getrange("name", 1,3), 14);
            
            //返回key值字符串长度的值
            printByIndex(jedis.strlen("name").toString(),15);
            
            //清空所有数据
            jedis.flushDB();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {
        	jedis.close();
        }  
  
    }  
   
   public static void printByIndex(String s,int index){
	   System.out.println(index+" : "+s);
   }
  
    public static void ListKeyTest() {  
        Jedis jedis = JedisUtil.getJedis();  
        try {  
            // 开始前，先移除所有的内容  
        	jedis.flushDB();
        	//将String插入到messages对应的列表的末尾（最右边），与之相反的是lpush，插入到最左边，即列表的开始
        	//如果key值不存在则创建一个新的列表，如果key值存在但不是一个list对象则抛出异常
            jedis.rpush("messages", "Hello how are you?");  
            jedis.rpush("messages", "Fine thanks. I'm having fun with redis.");  
            jedis.rpush("messages", "I should look into this NOSQL thing ASAP");  
            
            
            // 取出key值对应的列表的指定范围的数据，若超出列表范围不报错，起始位置超过列表范围返回空列表  
            // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有  
            List<String> values = jedis.lrange("messages", 0, -1);  
            values.forEach(print);
            
            printByIndex(jedis.llen("messages").toString(), 1);
            //将key值对应的列表截断成指定范围的列表，超出列表范围不报错
            jedis.ltrim("messages", 0,1);
            printByIndex(jedis.llen("messages").toString(), 1);
            
            jedis.lpush("messages","shl");
            jedis.lpush("messages","shl2");
            jedis.lpush("messages","shl3");
            //取出key值对应列表中对应序号的元素位置
            printByIndex(jedis.lindex("messages", 1), 2);
            
            //将key值对应列表中对应序号的元素替换成指定值,返回ok或者fail
            printByIndex(jedis.lset("messages", 1, "lset"),3);
            printByIndex(jedis.lindex("messages", 1), 3);
  
            List<String> all=jedis.lrange("messages", 0, -1);
            all.forEach(print);
            //移除位置为key值对应的列表中倒数第二个元素且值为指定值的起点之后的元素，返回被移除的元素的个数
            jedis.lrem("messages",-2,"Fine thanks. I'm having fun with redis.");
            printByIndex(jedis.llen("messages").toString(), 4);
            
            //移除key值对应列表的首个元素，返回该该元素，与之相反的是rpop，从列表的最后移除并返回一个元素
            //blpop或者brpop为对应方法的阻塞版本，即列表中没有元素时会处于阻塞的状态，直到有元素时才返回
            jedis.lpop("messages");
            printByIndex(jedis.llen("messages").toString(), 5);
            
            jedis.lpush("name", "sun","sun2","sun3");
            //把messages对应的列表的最后一个元素移除并插入到name对应的列表的起始位置，并返回该元素
            jedis.rpoplpush("messages", "name");
            all =jedis.lrange("name", 0, -1);
            all.forEach(print);
            
            //对一个key值为list或者sort类型的值进行排序，可以指定排序方式
            //没有指定排序条件的情况下默认将列表中的元素转化为数值升序排序，若不能转化为数值则报错
            //返回排序后的列表，原列表没有改变
//            jedis.sort("name");
            jedis.lpush("age", "1","5","2","4","3");
            all=jedis.sort("age");
            all.forEach(print);
            
            //在指定key值的list中指定元素5之后插入新的元素7
            jedis.linsert("age",BinaryClient.LIST_POSITION.AFTER, "5", "7");
            printCollection(jedis.lrange("age", 0, -1));
            
            jedis.set("name", "sunhongliang");
            //指定覆写key值时的偏移量，即从第4个字符开始覆写
            jedis.setrange("name", 4, "shl");
            printByIndex(jedis.get("name"), 6);
            
            //返回指定key值的字符串中指定起始偏移量的字符串
            printByIndex(jedis.getrange("name", 0, 6),7);
            
            jedis.flushDB();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            jedis.close();
        }  
  
    }  
  
    public static void SetKeyTest() {  
        Jedis jedis = JedisUtil.getJedis();  
        try {
        	//往key值为set类型的set列表中添加元素，若该元素在列表中已存在则不进行任何操作，若key值对应的列表不存在则创建
        	//该列表并添加元素，若key值对应的值类型不是set类型则报错
            jedis.sadd("myset", "1");  
            jedis.sadd("myset", "2");  
            jedis.sadd("myset", "3");  
            jedis.sadd("myset", "4");
            
            // 移除key值对应set中对应的元素，若元素不存在不报错
            jedis.srem("myset", "4","1","5");  
            Set<String> all =jedis.smembers("myset");
            all.forEach(print);
            
            jedis.sadd("myset","7","8","9");
            //从myset中随机选取一个元素并移除，srandmember是随机选取一个元素并不移除
            jedis.spop("myset");
            
            jedis.sadd("age","12","13","14","7","1","2");
            //把指定元素从源key对应的set中移动到目标key对应的set中,成功返回1
            printByIndex(jedis.smove("myset", "age", "9").toString(),1);
            all=jedis.smembers("age");
            all.forEach(print);
            
            //返回key值对应set中元素的个数
            printByIndex(jedis.scard("myset").toString(), 2);
            
            printByIndex(jedis.sismember("myset","7").toString(), 3);
           
            //返回所有key值对应set中共有的元素，即取交集，如果有一个key值对应的set不存在则返回空列表
            all=jedis.sinter("myset","age");
            all.forEach(print);
            
            //将目标key值对应的set元素存放到目标key中
            jedis.sinterstore("shl", "myset","age");
            all=jedis.smembers("shl");
            all.forEach(print);
            
            //取key值对应set的并集，重载版本sunionstore是将交集结果存储到指定key中
            all=jedis.sunion("myset","age");
            all.forEach(print);
            
            //取差集
            all=jedis.sdiff("myset","age");
            all.forEach(print);
            
            jedis.flushDB();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            jedis.close();
        }  
  
    }  
  
    public static void SortedSetKeyTest() {  
        Jedis jedis = JedisUtil.getJedis();  
        try {
        	//往key值类型为SortedSet的set类型中添加数据，double值是排序依据，若key存在但对应的值类型不是的sortedset则报异常
            jedis.zadd("hackers", 1940, "Alan Kay");  
            jedis.zadd("hackers", 1953, "Richard Stallman");  
            jedis.zadd("hackers", 1965, "Yukihiro Matsumoto");  
            jedis.zadd("hackers", 1916, "Claude Shannon");  
            jedis.zadd("hackers", 1969, "Linus Torvalds");  
            jedis.zadd("hackers", 1912, "Alan Turing");  
            
            //取出key值对应sortedset中指定范围的元素，排序顺序按照score从低到高排序，zrevrange与之相反，从高到低排序
            Set<String> setValues = jedis.zrange("hackers", 0, -1);  
            setValues.forEach(print);
            
            //移除指定key对应sortedset中指定的元素，成功返回1
            printByIndex(jedis.zrem("hackers", "Alan Kay","shl").toString(), 1);
           
            //给key对应的sortedset中指定元素的score增加1968，若指定元素不存在则创建一个新的元素且score为1968，然后重新排序
            printByIndex(jedis.zincrby("hackers", 1968, "shl").toString(), 2);
            
            //返回key对应sortedset列表中指定元素的序号，该序号按照score从低到高排序产生，zrevrank与之相反是 按照从高到低排序
            printByIndex(jedis.zrank("hackers", "shl").toString(), 3);
            
            //返回key值对应sortedset列表中元素的个数
            printByIndex(jedis.zcard("hackers").toString(), 4);
            
            //返回key值对应sortedset列表中指定元素的score
            printByIndex(jedis.zscore("hackers", "shl").toString(), 5);
            
            //返回key值对应sortedset中score两个score之间的元素的个数
            printByIndex(jedis.zcount("hackers", 1916, 1960).toString(), 6);
            
            //返回key值对应sortedset中score两个score之间的所有的元素
            setValues=jedis.zrangeByScore("hackers", 1916, 1960, 0, -1); 
            printCollection(setValues);
            
            //按照score从低到高排序，移除指定起始位置的所有元素，返回移除元素的个数
            printByIndex(jedis.zremrangeByRank("hackers", -1,-2).toString(), 7);
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
        	jedis.close();
        }  
  
    }  
  
    public static void HashsetKeyTest() {  
        Jedis jedis = JedisUtil.getJedis();  
        try {  
        	//hashset为key值时，往name对应的hashset中添加键值对
            jedis.hset("name", "name","shl");
            jedis.hset("name", "name2","shl2");
            jedis.hset("name", "name3","shl3");
            jedis.hset("name", "name","shl4");
            //获取name对应的hashset中的key值为name的键值
            printByIndex(jedis.hget("name", "name"),1);
            printByIndex(jedis.hget("name", "name2"),1);

            Map<String, String> pairs = new HashMap<String, String>();  
            pairs.put("name", "Akshi");  
            pairs.put("age", "2");  
            pairs.put("sex", "Female");
            jedis.hmset("kid", pairs);  
            List<String> name = jedis.hmget("kid", "name","age","sex");// 结果是个泛型的LIST  
            name.forEach(s->System.out.print(" "+s+" "));
            
            jedis.hincrBy("kid", "age", 10);
            printByIndex(jedis.hget("kid", "age"), 2);
            
            //返回指定key值对应的hashset的键值对数量
            printByIndex(jedis.hlen("name").toString(), 3);
            
            //返回指定key值对应的hashset中的所有key值或者value值
            Set<String> keys=jedis.hkeys("kid");
            keys.forEach(print);
            List<String> vals=jedis.hvals("kid");
            vals.forEach(print);
            Map<String, String> all=jedis.hgetAll("kid");
            all.forEach((s,y)->System.out.println(s+" : "+y));
            
            jedis.flushDB();
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
        	jedis.close();
        }  
    }  
  
    public static void printCollection(Collection<String> result){
    	result.forEach(print);
    	System.out.println("\n============================");
    }
    public static void SortTest(){
    	Jedis jedis=JedisUtil.getJedis();
    	jedis.flushDB();
    	jedis.lpush("x", "2","5","4","3","1");
    	jedis.lpush("y", "e","a","c","s","g");
    	List<String> all=jedis.sort("x");
    	System.out.println(jedis.llen("x").toString());
    	printCollection(all);
    	//默认将set或者list中的元素转化为数字然后升序排序，无法转化则报错
    	//all=jedis.sort("y");
    	//printCollection(all);
    	
    	SortingParams params=new SortingParams();
    	all=jedis.sort("x", params.desc());
    	printCollection(all);
    	
    	
    	//limit中第一个参数是起始位置，第二个参数是个数
    	all=jedis.sort("x",params.desc().limit(1, 3));
    	System.out.println(jedis.llen("x").toString());
    	printCollection(all);
    	
//    	all=jedis.sort("y", params.alpha().asc().limit(0, 4));
    	all=jedis.sort("y", params.alpha().asc());
    	System.out.println(jedis.llen("y").toString());
    	printCollection(all);
    	
    	
    	jedis.set("key1", "5");
    	jedis.set("key2", "4");
    	jedis.set("key3", "3");
    	jedis.set("key4", "2");
    	jedis.set("key5", "1");
    	
    	jedis.set("shl1", "7");
    	jedis.set("shl2", "9");
    	jedis.set("shl3", "10");
    	jedis.set("shl4", "6");
    	jedis.set("shl5", "11");
    	//假定x为1对应key1/shl1，x为2对应key2/shl2，by("key*")表示按照x的值对应的key*的值排序,get("shl*")表示取出对应shl*的值
    	//注意params前一次执行的limit操作会对后面的输出结果产生影响，即使不显示调用limit
    	all=jedis.sort("x", params.by("key*").desc());
    	printCollection(all);
    	all=jedis.sort("x", params.by("key*").desc().get("shl*"));
    	printCollection(all);
    	jedis.flushDB();
    	jedis.close();
    }
  
    
  
    /** 
     * sort list 
     * LIST结合hash的排序 
     */  
    public void SortTest2() {  
        Jedis jedis = JedisUtil.getJedis();  
        jedis.del("user:66", "user:55", "user:33", "user:22", "user:11",  
                "userlist");  
        jedis.lpush("userlist", "33");  
        jedis.lpush("userlist", "22");  
        jedis.lpush("userlist", "55");  
        jedis.lpush("userlist", "11");  
  
        jedis.hset("user:66", "name", "66");  
        jedis.hset("user:55", "name", "55");  
        jedis.hset("user:33", "name", "33");  
        jedis.hset("user:22", "name", "79");  
        jedis.hset("user:11", "name", "24");  
        jedis.hset("user:11", "add", "beijing");  
        jedis.hset("user:22", "add", "shanghai");  
        jedis.hset("user:33", "add", "guangzhou");  
        jedis.hset("user:55", "add", "chongqing");  
        jedis.hset("user:66", "add", "xi'an");  
  
        SortingParams sortingParameters = new SortingParams();  
        // 符号 "->" 用于分割哈希表的键名(key name)和索引域(hash field)，格式为 "key->field" 。  
        sortingParameters.get("user:*->name");  
        sortingParameters.get("user:*->add");  
        sortingParameters.by("user:*->name"); 
        //#表示取出当前排序对象即userlist
        sortingParameters.get("#");  
        List<String> result = jedis.sort("userlist", sortingParameters);  
        printCollection(result); 
        jedis.flushDB();
        jedis.close();
    }  
  
    /** 
     * sort set 
     * SET结合String的排序 
     */  
    public void SortTest3() {  
        Jedis jedis = JedisUtil.getJedis();  
        jedis.del("tom:friend:list", "score:uid:123", "score:uid:456",  
                "score:uid:789", "score:uid:101", "uid:123", "uid:456",  
                "uid:789", "uid:101");  
  
        jedis.sadd("tom:friend:list", "123"); // tom的好友列表  
        jedis.sadd("tom:friend:list", "456");  
        jedis.sadd("tom:friend:list", "789");  
        jedis.sadd("tom:friend:list", "101");  
  
        jedis.set("score:uid:123", "1000"); // 好友对应的成绩  
        jedis.set("score:uid:456", "6000");  
        jedis.set("score:uid:789", "100");  
        jedis.set("score:uid:101", "5999");  
  
        jedis.set("uid:123", "{'uid':123,'name':'lucy'}"); // 好友的详细信息  
        jedis.set("uid:456", "{'uid':456,'name':'jack'}");  
        jedis.set("uid:789", "{'uid':789,'name':'jay'}");  
        jedis.set("uid:101", "{'uid':101,'name':'jolin'}");  
  
        SortingParams sortingParameters = new SortingParams();  
  
        sortingParameters.desc();  
        // sortingParameters.limit(0, 2);  
        // 注意GET操作是有序的，GET user_name_* GET user_password_*  
        // 和 GET user_password_* GET user_name_*返回的结果位置不同  
        sortingParameters.get("#");// GET 还有一个特殊的规则—— "GET #"  
                                    // ，用于获取被排序对象(我们这里的例子是 user_id )的当前元素。  
        sortingParameters.get("uid:*");  
        sortingParameters.get("score:uid:*");  
        sortingParameters.by("score:uid:*");  
        // 对应的redis 命令是./redis-cli sort tom:friend:list by score:uid:* get # get  
        // uid:* get score:uid:*  
        List<String> result = jedis.sort("tom:friend:list", sortingParameters);  
        printCollection(result);
        jedis.flushDB();
        jedis.close();
    }  
  
    public void testDB() {  
        Jedis jedis = JedisUtil.getJedis();  
        // 通过索引选择数据库，默认连接的数据库所有是0,默认数据库数是16个。返回1表示成功，0失败  
        System.out.println(jedis.select(0));
        
       // dbsize 返回当前数据库的key数量  
        System.out.println(jedis.dbSize());
       
        // 返回匹配指定模式的所有key
        System.out.println(jedis.keys("*")); 
        
        //返回随机的一个key值
        System.out.println(jedis.randomKey());  
        
        //获取指定匹配模式下的配置参数，与之对应的是configSet
        System.out.println(jedis.configGet("save"));
        
        //设置指定配置参数，如下所示设置连接密码，注意修改的配置是当前服务器在内存中的配置，可能与配置文件不同
        //可以通过config Rewrite命令将当前服务器配置写入配置文件中，jedis似乎没有实现该命令
        jedis.configSet("requirepass", "123456");
        
       //终止当前客户端连接
        jedis.quit();
        //验证当前连接是否有效,若服务器正常工作返回pang
        jedis=new Jedis("172.27.12.85", 6379);
        //输入连接密码,若没有设置密码，输入密码也会报错
//        jedis.auth("123456");
        //必须在ping前输入密码，否则报权限异常
        System.out.println(jedis.ping());
        
        
        //返回所有的客户端信息，对应的是clientkill，关掉指定的客户端
        System.out.println(jedis.clientList());
        
        //为当前连接设置名字
        jedis.clientSetname("shl");
        System.out.println(jedis.clientGetname());
        
        //返回当前服务器的运行状况和配置等信息，可以通过configrestat刷新info信息
        System.out.println(jedis.info());
        
        //返回最后一次将数据保存进磁盘的时间
        System.out.println(jedis.lastsave());
        
        //打印发送到服务器的命令
        jedis.monitor(new JedisMonitor() {
			@Override
			public void onCommand(String command) {
				System.out.println(command);
			}
		});
       
        //表示通过异步的方式将当前数据快照写入磁盘，同步方式为save，会阻塞客户端操作
        jedis.bgsave();
        
        //将当前服务器变成指定服务器的从属服务器
        //jedis.slaveof("172.27.12.85", 6380);
        //将当前服务器变成主服务器
//        jedis.slaveofNoOne();
        
        
        // 删除当前数据库中所有key,此方法不会失败。慎用  
        jedis.flushDB();
       // 删除所有数据库中的所有key，此方法不会失败。更加慎用  
        jedis.flushAll();
        //关闭服务器
//        jedis.shutdown();
        
    }  
    
    public void PipelineTest() {  
        long start = new Date().getTime();  
  
        Jedis jedis = JedisUtil.getJedis();  
        for (int i = 0; i < 10000; i++) {  
            jedis.set("age1" + i, i + "");  
            jedis.get("age1" + i);// 每个操作都发送请求给redis-server  
        }  
        long end = new Date().getTime();  
  
        System.out.println("unuse pipeline cost:" + (end - start) + "ms");  
  
        start = new Date().getTime();  
        
        Pipeline p = jedis.pipelined();  
        for (int i = 0; i < 10000; i++) {  
            p.set("age2" + i, i + "");  
            System.out.println(p.get("age2" + i));  
        }  
     // 这段代码获取所有的response  
        p.sync();
        //不同步等待其返回结果
//        p.syncAndReturnAll();
  
        end = new Date().getTime();  
  
        System.out.println("use pipeline cost:" + (end - start) + "ms");  
        
        jedis.flushDB(); 
        jedis.close();  
    }
    
    public static void TransactionTest(){
        Jedis jedis=JedisUtil.getJedis();
        //开启事务
    	Transaction tx = jedis.multi();  
        for (int i = 0; i < 10; i++) {  
            tx.set("t"+i, "t"+i);  
        }  
        //监控若干若干key值，若在事务操作期间key值发生改变则该事务操作失败，抛出异常
        //但是已经执行的事务操作不会回滚
        System.out.println(jedis.watch("t1","t2"));  
         
        //通过discard方法取消事务
        //tx.discard();  
        
        //提交事务
        List<Object> results = tx.exec();  
        
        System.out.println(jedis.get("t1"));
        jedis.close();  
    }
    
    @Test
    public void ShardedJedisTest(){ 
    	//注意从机必须可写
        List<JedisShardInfo> shards = Arrays.asList(  
                new JedisShardInfo("172.27.12.85", 6379),  
                new JedisShardInfo("172.27.12.85", 6380)  
                );  
        
        //建立一个采用一致性哈希算法的集群
        ShardedJedis sharding = new ShardedJedis(shards);  
          
        for (int i = 0; i < 10; i++) {  
            String result = sharding.set("sn" + i, "n" + i);  
            System.out.println(result);  
        }
        Jedis jedis=new Jedis("172.27.12.85", 6380);
        System.out.println(jedis.get("sn2"));
        jedis.close();
        jedis=new Jedis("172.27.12.85", 6379);
        System.out.println(jedis.get("sn2"));
        jedis.close();
        
        sharding.disconnect();  
    }  
      
    //分布式直连管道异步调用  
    @Test  
    public void ShardedJedisTest2() {  
        List<JedisShardInfo> shards = Arrays.asList(  
        		 new JedisShardInfo("172.27.12.85", 6379),  
                 new JedisShardInfo("172.27.12.85", 6380)    
                );  
  
        ShardedJedis sharding = new ShardedJedis(shards);  
  
        ShardedJedisPipeline pipeline = sharding.pipelined();  
        long start = System.currentTimeMillis();  
        for (int i = 0; i < 100000; i++) {  
            pipeline.set("sp" + i, "p" + i);  
        }  
        List<Object> results = pipeline.syncAndReturnAll();  
        long end = System.currentTimeMillis();  
        System.out.println("Pipelined@Sharing SET: " + ((end - start)/1000.0) + " seconds");  
  
        sharding.disconnect();  
    }  
      
    //分布式连接池同步调用  
    //分布式调用代码是运行在线程中，那么上面两个直连调用方式就不合适了，  
    //因为直连方式是非线程安全的，这个时候，你就必须选择连接池调用  
    @Test  
    public void ShardedJedisTest3() {  
        List<JedisShardInfo> shards = Arrays.asList(  
        		 new JedisShardInfo("172.27.12.85", 6379),  
                 new JedisShardInfo("172.27.12.85", 6380)   
                );  
  
        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);  
  
        ShardedJedis one = pool.getResource();  
  
        long start = System.currentTimeMillis();  
        for (int i = 0; i < 10; i++) {  
            String result = one.set("spn" + i, "n" + i);  
            System.out.println(result);  
        }  
        long end = System.currentTimeMillis();  
        one.close();
        System.out.println("Simple@Pool SET: " + ((end - start)/1000.0) + " seconds");  
  
        pool.destroy();  
    }  
    
  //分布式连接池异步调用  
    @Test  
    public void ShardedJedisTest4() {  
        List<JedisShardInfo> shards = Arrays.asList(  
        		new JedisShardInfo("172.27.12.85", 6379),  
                new JedisShardInfo("172.27.12.85", 6380)
                );  
  
        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);  
  
        ShardedJedis one = pool.getResource();  
  
        ShardedJedisPipeline pipeline = one.pipelined();  
  
        long start = System.currentTimeMillis();  
        for (int i = 0; i < 100000; i++) {  
            pipeline.set("sppn" + i, "n" + i);  
        }  
        List<Object> results = pipeline.syncAndReturnAll();  
        long end = System.currentTimeMillis();  
        one.close();  
        System.out.println("Pipelined@Pool SET: " + ((end - start)/1000.0) + " seconds");  
        pool.destroy();  
    }  
    
    @Test
    public void PubandScribeTest(){
    	 Jedis redisClient = JedisUtil.getJedis();  
    	  MyListener listener = new MyListener();  
    	  Runnable scribe=()->{
    		  //监听指定的频道
    		  redisClient.subscribe(listener, "shl");
    	  };
    	  scribe.run();
    	 
    }
    @Test
    public void PubandScribeTest2(){
    	Jedis redisClient = JedisUtil.getJedis();  
    	//发布消息，注意先运行PubandScribeTest再运行PubandScribeTest2
    	redisClient.publish("shl", "who are you ");
    	redisClient.publish("shl", "I am sunhongliang");
    }
    
    @Test
    public void CluserTest(){
    	//注意执行测试前必须先通过redis-trib.rb工具建立集群
    	//在启动各集群节点后执行flushall 和  cluster reset soft命令清空已有的槽位（slot），否则报？？？ slot is busy错误
    	JedisPoolConfig config = new JedisPoolConfig();  
        config.setMaxTotal(20);  
        config.setMaxIdle(2);  
  
        HostAndPort hp0 = new HostAndPort("172.27.12.85", 6379);  
        HostAndPort hp1 = new HostAndPort("172.27.12.85", 6380);  
        HostAndPort hp2 = new HostAndPort("172.27.12.85", 6381);  
  
        Set<HostAndPort> hps = new HashSet<HostAndPort>();  
        hps.add(hp0);  
        hps.add(hp1);  
        hps.add(hp2);  
  
        // 超时，最大的转发数，最大链接数，最小链接数都会影响到集群  
        //超时时间不宜设置得过短否则报没有可用的节点
        JedisCluster jedisCluster = new JedisCluster(hps, 5000, 10, config);  
        Jedis jedis=new Jedis("172.27.12.85", 6379);  
       
        //查看集群节点相关信息命令redis-cli -p 6379 cluster nodes
//        jedisCluster.getClusterNodes();
          jedis.clusterNodes();
        
          //查看集群的状态：redis-cli -p 6379 cluster info
          jedis.clusterInfo();
        
          //获取指定key的槽位
          System.out.println(jedis.clusterKeySlot("shl"));
        
          long start = System.currentTimeMillis();  
        for (int i = 0; i < 1000; i++) {  
            jedisCluster.set("sn" + i, "n" + i);  
        }  
  
        for (int i = 0; i < 1000; i++) {  
            System.out.println(jedisCluster.get("sn" + i));  
        }  
  
        long end = System.currentTimeMillis();  
        System.out.println("Simple  @ Sharding Set : " + (end - start) );  
        
        try {
			jedisCluster.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("关闭集群失败",e);
		}  
    }
    
    @Test
    public void testJedis() throws InterruptedException {

        Set<String> sentinels = new HashSet<String>();
        sentinels.add("10.6.144.155:7031");
        sentinels.add("10.6.144.156:7031");        

        JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster",
                sentinels);

        Jedis jedis = sentinelPool.getResource();

        System.out.println("current Host:"
                + sentinelPool.getCurrentHostMaster());

        String key = "a";

        String cacheData = jedis.get(key);

        if (cacheData == null) {
            jedis.del(key);
        }

        jedis.set(key, "aaa");// 写入

        System.out.println(jedis.get(key));// 读取

        System.out.println("current Host:"
                + sentinelPool.getCurrentHostMaster());// down掉master，观察slave是否被提升为master

        jedis.set(key, "bbb");// 测试新master的写入

        System.out.println(jedis.get(key));// 观察读取是否正常

        sentinelPool.close();
        jedis.close();

    }
}  

    