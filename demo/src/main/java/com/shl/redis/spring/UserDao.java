package com.shl.redis.spring;
public interface UserDao {  
    /** 
     * @param uid 
     * @param address 
     */  
    void save(final User user);  
  
    /** 
     * @param uid 
     * @return 
     */  
    User read(final String uid);  
  
    /** 
     * @param uid 
     */  
    void delete(final String uid);  
}  


