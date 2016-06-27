package com.minitwit.dao;

import java.util.List;
import java.util.Map;

import com.minitwit.model.Message;
import com.minitwit.model.User;

public interface MessageDao {
	/** 根据用户id查找该用户发布的消息
	 * @param user
	 * @return
	 */
	List<Message> getUserTimelineMessages(User user);
	
	/** 根据用户id查找该用户发布的回复的消息
	 * @param user
	 * @return
	 */
	List<Message> getUserFullTimelineMessages(User user);
	
	/** 查询所有的消息
	 * @return
	 */
	List<Message> getPublicTimelineMessages();
	
	/** 发布消息
	 * @param m
	 */
	void insertMessage(Message m);
	
	/** 根据id删除用户消息
	 * @param id
	 */
	void deleteMessage(int id);
	
	/** 修改用户消息
	 * @param m
	 */
	void updateMessage(Message m);
	
	/**  根据由指定条件合成的sql语句查询指定的message信息
	 * @param sql
	 * @return
	 */
	List<Message> getMessageByCondition(String sql,Map<String, Object> params);
    
	/** 根据id查找消息
     * @param id
     * @return
     */
    Message getMessageById(int id);
}
