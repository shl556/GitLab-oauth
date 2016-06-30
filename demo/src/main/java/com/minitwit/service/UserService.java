package com.minitwit.service;

import com.minitwit.model.Error;
import com.minitwit.model.User;

public interface UserService {
	/** 根据用户名查找用户信息
	 * @param username
	 * @return
	 */
	User getUserbyUsername(String username);
	
	/** 插入一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	Error insertFollower(User follower, User followee);
	
	/**删除一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	Error  deleteFollower(User follower, User followee);
	
	/** 查看关注人与被关注人信息是否存在
	 * @param follower
	 * @param followee
	 * @return
	 */
	Error isUserFollower(User follower, User followee);
	
	/** 注册用户信息
	 * @param user
	 */
	Error registerUser(User user);
	
	/**  检查cookie中的用户名与密码是否匹配,若匹配返回User
	 * @param userName
	 * @param password
	 * @return
	 */
	User  autoLogin(String cookieString);
	
	/** 用户登陆，检测用户名与密码是否匹配
	 * @param user
	 * @return
	 */
	User checkUser(User user);
}
