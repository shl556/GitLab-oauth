package com.minitwit.dao;

import com.minitwit.model.User;

public interface UserDao {

	/** 根据用户名查找用户信息
	 * @param username
	 * @return
	 */
	User getUserbyUsername(String username);
	
	/** 插入一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	void insertFollower(User follower, User followee);
	
	/**删除一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	void deleteFollower(User follower, User followee);
	
	/** 查看关注人与被关注人信息是否存在
	 * @param follower
	 * @param followee
	 * @return
	 */
	boolean isUserFollower(User follower, User followee);
	
	/** 注册用户信息
	 * @param user
	 */
	void registerUser(User user);
}
