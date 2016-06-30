package com.minitwit.service;

import java.util.List;
import java.util.Map;

import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.model.Condition;
import com.minitwit.model.Error;

public interface MessageService {
	
	/**根据用户id查找该用户发布的消息
	 * @param id  用户id
	 * @param user 当前用户对象
	 * @return
	 */
	User getUserTimelineMessages(int id,User user);
	
	
	/** 根据用户id查找该用户发布的回复消息
	 * @param id 用户id
	 * @param user 当前用户对象
	 * @return
	 */
	User getUserFullTimelineMessages(int id,User user);
	
	/** 查询所有的消息
	 * @return
	 */
	List<Message> getPublicTimelineMessages();
	
	/** 发布消息
	 * @param m
	 */
	Error insertMessage(Message m);
	
	/** 根据id删除用户消息
	 * @param id
	 */
	Error deleteMessage(int id);
	
	/** 修改用户消息
	 * @param m
	 */
	Error updateMessage(Message m);
	
	/**  根据由指定条件查询指定的message信息
	 * @param sql
	 * @return
	 */
    User getMessageByCondition(User user,Condition condition);
    
	/** 根据id查找消息
     * @param id
     * @return
     */
    Message getMessageById(int id);
}
