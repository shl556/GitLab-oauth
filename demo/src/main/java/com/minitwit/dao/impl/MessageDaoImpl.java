package com.minitwit.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.minitwit.dao.MessageDao;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.util.GravatarUtil;

@Repository
public class MessageDaoImpl implements MessageDao {
	
	private static final String GRAVATAR_DEFAULT_IMAGE_TYPE = "monsterid";
	private static final int GRAVATAR_SIZE = 48;
	private NamedParameterJdbcTemplate template;
    private Logger log=LoggerFactory.getLogger(getClass());
	@Autowired
	public MessageDaoImpl(DataSource ds) {
		template = new NamedParameterJdbcTemplate(ds);
	}

	
	@Override
	public List<Message> getUserTimelineMessages(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        
		String sql = "select message.*, user.* from message, user where " +
				"user.user_id = message.author_id and user.user_id = :id " +
				"order by message.pub_date desc";
		List<Message> result = template.query(sql, params, messageMapper);
		log.info("根据用户id查找该该用户所发布的信息成功，用户id{}，共查找相关信息{}条",id,result.size());
		return result;
	}

	@Override
	public List<Message> getUserFullTimelineMessages(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        
		String sql = "select message.*, user.* from message, user " +
				"where message.author_id = user.user_id and ( " +
				"user.user_id = :id or " +
				"user.user_id in (select followee_id from follower " +
                                    "where follower_id = :id))" +
                "order by message.pub_date desc";
		List<Message> result = template.query(sql, params, messageMapper);
		log.info("根据用户id查找该用户及其所关注用户所发布的信息成功，用户id:{}，共查找相关信息{}条",id,result.size());
		return result;
	}

	@Override
	public List<Message> getPublicTimelineMessages() {
		Map<String, Object> params = new HashMap<String, Object>();
        
		String sql = "select message.*, user.* from message, user " +
				"where message.author_id = user.user_id " +
				"order by message.pub_date desc";
		List<Message> result = template.query(sql, params, messageMapper);
		log.info("查找所有发布的信息成功，共查找相关信息{}条",result.size());
		return result;
	}

	@Override
	public void insertMessage(Message m) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", m.getUserId());
        params.put("text", m.getText());
        params.put("pubDate", m.getPubDate());
        
        String sql = "insert into message (author_id, text, pub_date) values (:userId, :text, :pubDate)";
        template.update(sql, params);
         log.info("插入一条消息成功，消息主要内容："+m);
	}
	
	private RowMapper<Message> messageMapper = (rs, rowNum) -> {
		Message m = new Message();
		
		m.setId(rs.getInt("message_id"));
		m.setUserId(rs.getInt("author_id"));
		m.setUsername(rs.getString("username"));
		m.setText(rs.getString("text"));
		m.setPubDate(rs.getTimestamp("pub_date"));
		m.setGravatar(GravatarUtil.gravatarURL(rs.getString("email"), GRAVATAR_DEFAULT_IMAGE_TYPE, GRAVATAR_SIZE));
		
		return m;
	};
	@Override
	public void deleteMessage(int id) {
		   Map<String, Object> params=new HashMap<>();
		   params.put("id", id);
		   String sql="delete from message a where a.message_id= :id";
		   template.update(sql, params);
		   log.info("删除id为{}的消息成功",id);
	}


	@Override
	public void updateMessage(Message m) {
          Map<String, Object> params=new HashMap<>();
          params.put("id", m.getId());
          params.put("text", m.getText());
          params.put("pubDate", m.getPubDate());
          String sql="update message  a set a.text =:text , a.pub_date=:pubDate where a.message_id=:id";
          int i=template.update(sql, params);
//          System.out.println("更新数据库，受影响的行数："+i+" 消息id："+m.getId());
          log.info("更新消息成功，更新后的消息为：{}",m);
	}


	@Override
	public List<Message> getMessageByCondition(String sql,Map<String, Object> params) {
		log.info("合成sql语句：{}",sql);
		List<Message> messsages= template.query(sql, params, messageMapper);
		    return messsages;
	}

	@Override
	public Message getMessageById(int id) {
		Map<String, Object> params=new HashMap<>();
		params.put("id", id);
		String sql = "select message.*, user.* from message, user where " +
				"user.user_id = message.author_id and message.message_id= :id ";
		List<Message> messages=template.query(sql, params, messageMapper);
		Message message=new Message();
		if(!messages.isEmpty()){
			message=messages.get(0);
			log.info("根据id查找message成功，消息为："+message);
		}
		return message;
	}

}
