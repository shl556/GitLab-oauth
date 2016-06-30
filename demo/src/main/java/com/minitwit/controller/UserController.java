package com.minitwit.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.minitwit.model.Error;
import com.minitwit.model.User;
import com.minitwit.service.UserService;

@RestController
@RequestMapping("/users")
@SessionAttributes("user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//提供类似于Strust的同名参数绑定功能
	@InitBinder("follower")  
	public void initAccountBinder(WebDataBinder binder) {  
	    binder.setFieldDefaultPrefix("follower.");  
	} 

	@InitBinder("followee")  
	public void initUserBinder(WebDataBinder binder) {  
	    binder.setFieldDefaultPrefix("followee.");  
	}
	
	/** 根据用户名查找用户信息
	 * @param username
	 * @return
	 */
	@RequestMapping(value="/{username}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserbyUsername(@PathVariable("username") String username,HttpSession session){
//		System.out.println("username:"+username);
//		System.out.println("userService为空:"+(userService==null));
		User user=userService.getUserbyUsername(username);
		session.setAttribute("user", user);
		return user;
	}
	
	/** 插入一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	@RequestMapping(value="/follower",method=RequestMethod.PUT,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error insertFollower(@ModelAttribute("follower") User follower, @ModelAttribute("followee") User followee){
		return userService.insertFollower(follower, followee);
	}
	
	/**删除一条关注人与被关注人的记录
	 * @param follower
	 * @param followee
	 */
	@RequestMapping(value="/follower",method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error  deleteFollower(@ModelAttribute("follower") User follower, @ModelAttribute("followee") User followee){
		return userService.deleteFollower(follower, followee);
	}
	
	/** 查看关注人与被关注人信息是否存在
	 * @param follower
	 * @param followee
	 * @return
	 */
	@RequestMapping(value="/follower",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error isUserFollower(@ModelAttribute("follower") User follower, @ModelAttribute("followee") User followee){
//		System.out.println("follower.id="+follower.getId());
//		System.out.println("followee.id="+followee.getId());
		return userService.isUserFollower(follower, followee);
	}
	
	/** 注册用户信息
	 * @param user
	 */
	@RequestMapping(method=RequestMethod.PUT,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Error registerUser(User user){
		return userService.registerUser(user);
	}
	
	/**  检查cookie中的用户名与密码是否匹配,若匹配返回User
	 * @param userName
	 * @param password
	 * @return
	 */
	@ModelAttribute("user") 
	@RequestMapping(value="/autologin",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User  autoLogin(@CookieValue("autologin") String cookieString){
		return userService.autoLogin(cookieString);
	}
	
	/** 用户登陆，检测用户名与密码是否匹配
	 * @param user
	 * @return
	 */
	@ModelAttribute("user")
	@RequestMapping(value="/login",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User checkUser(User user){
		return userService.checkUser(user);
	}
}
