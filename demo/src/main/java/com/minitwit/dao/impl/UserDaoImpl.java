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

import com.minitwit.dao.UserDao;
import com.minitwit.model.User;

@Repository
public class UserDaoImpl implements UserDao {
	
	private NamedParameterJdbcTemplate template;
	private Logger log=LoggerFactory.getLogger(getClass());

	@Autowired
	public UserDaoImpl(DataSource ds) {
		template = new NamedParameterJdbcTemplate(ds);
	}

	@Override
	public User getUserbyUsername(String username) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", username);
        
		String sql = "SELECT * FROM user WHERE username=:name";
		
        List<User> list = template.query(
                    sql,
                    params,
                    userMapper);
        
        User result = null;
        if(list != null && !list.isEmpty()) {
        	result = list.get(0);
        	log.info("用户名已存在，返回用户信息：{}",result);
        }else{
        log.info("用户名{}不存在",username);
        }
		return result;
	}

	@Override
	public void insertFollower(User follower, User followee) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("follower", follower.getId());
        params.put("followee", followee.getId());
        
		String sql = "insert into follower (follower_id, followee_id) values (:follower, :followee)";
		
        template.update(sql,  params);
        log.info("插入关注者与被关注者信息成功，关注人id：{},被关注者id：{}",follower.getId(),followee.getId());
	}

	@Override
	public void deleteFollower(User follower, User followee) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("follower", follower.getId());
        params.put("followee", followee.getId());
        
		String sql = "delete from follower where follower_id = :follower and followee_id = :followee";
		
        template.update(sql,  params);
        log.info("删除关注者与被关注者信息成功，关注人id：{},被关注者id：{}",follower.getId(),followee.getId());
	}
	
	@Override
	public boolean isUserFollower(User follower, User followee) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("follower", follower.getId());
        params.put("followee", followee.getId());
        
		String sql = "select count(1) from follower where " +
            "follower.follower_id = :follower and follower.followee_id = :followee";
		
		Long l = template.queryForObject(sql, params, Long.class);
		if(l>0){
		log.info("查找关注者与被关注者信息成功，关注人id：{},被关注者id：{}",follower.getId(),followee.getId());
		}else{
			log.info("关注者与被关注者信息不存在，关注人id：{},被关注者id：{}",follower.getId(),followee.getId());
		}
		return l > 0;
	}

	@Override
	public void registerUser(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("pw", user.getPassword());
        
		String sql = "insert into user (username, email, pw) values (:username, :email, :pw)";
		
        template.update(sql,  params);
        log.info("注册用户成功，用户信息："+user.getUsername()+"/"+user.getEmail());
	}

	private RowMapper<User> userMapper = (rs, rowNum) -> {
		User u = new User();
		
		u.setId(rs.getInt("user_id"));
		u.setEmail(rs.getString("email"));
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("pw"));
		
		return u;
	};
}
