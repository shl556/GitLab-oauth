package com.minitwit.config;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minitwit.model.Condition;
import com.minitwit.model.LoginResult;
import com.minitwit.model.Message;
import com.minitwit.model.User;
import com.minitwit.service.impl.MiniTwitService;
import com.minitwit.template.FreeMarkerEngine;

import spark.ModelAndView;
import spark.Request;
import spark.utils.StringUtils;

public class WebConfig {

	private static final String USER_SESSION_ID = "user";
	private MiniTwitService service;
	private Logger log = LoggerFactory.getLogger(getClass());

	public WebConfig(MiniTwitService service) {
		this.service = service;
		staticFileLocation("/public");
		log.info("服务启动");
		setupRoutes();
		log.info("服务启动完成");
	}

	private void setupRoutes() {
		final String ECODING="UTF-8";
		final String VERSIONSTRING="2.3.23";
		// 配置全局的异常处理页面
		exception(Exception.class, (e, req, res) -> {
			String path = req.uri();
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8");
			String queryString = req.queryString();
			Map<String, String> params2 = req.params();
			String paramString = "";
			for (Map.Entry<String, String> entry : params2.entrySet()) {
				paramString += entry.getKey() + " :" + entry.getValue() + " / ";
			}
			log.info("访问路径{}，表单数据：" + params + ",请求字符串：" + queryString + ",路径参数：" + paramString, path, e);
		});

		/*
		 * 若用户未登陆则判断请求是否包含cookie，若有读取cookie中信息，判断账号与密码是否匹配
		 * 匹配成功则直接跳转至首页否则回到login页面
		 */
		before("/*", (req, res) -> {
			User authUser = getAuthenticatedUser(req);
			String path = req.uri().substring(1);
			System.out.println("访问路径：" + path);
			// 访问passPath中的路径时直接放行
			List<String> passPath = Arrays.asList("login", "register", "logout", "public", "css/style.css");
			if (authUser == null && !passPath.contains(path)) {
				String autoLogin = req.cookie("autologin");
				log.info("自动登陆cookie字符串：{}", autoLogin);
				if (autoLogin != null) {
					LoginResult result = service.autoLogin(autoLogin);
					if (result.getUser() != null) {
						log.info("用户自动登陆成功，用户信息：" + result.getUser());
						addAuthenticatedUser(req, result.getUser());
					} else {
						res.redirect("/index");
						halt();
					}
				}
			}
		});

		// 判断用户是否已经登陆，没有登陆跳转至登陆页面
		before("/auth/*", (req, res) -> {
			User user = getAuthenticatedUser(req);
			if (user == null) {
				res.redirect("/login");
				halt();
			}
		});

		/*
		 * 检查用户是否登陆，若已登录则跳转至timeline页面显示该用户发布和回回复的所有信息 否则跳转至公共首页，显示所有用户发布的消息
		 */
		get("/index", (req, res) -> {
			User user = getAuthenticatedUser(req);
			Map<String, Object> map = new HashMap<>();
			map.put("pageTitle", "Timel");
			map.put("user", user);
			List<Message> messages = service.getUserFullTimelineMessages(user);
			map.put("messages", messages);
			System.out.println("debu");
			return new ModelAndView(map, "timeline.ftl");
		}, new FreeMarkerEngine());

		before("/index", (req, res) -> {
			User user = getAuthenticatedUser(req);
			if (user == null) {
				res.redirect("/public");
				halt();
			}
		});

		// 显示所有用户发布的所有信息
		get("/public", (req, res) -> {
			User user = getAuthenticatedUser(req);
			Map<String, Object> map = new HashMap<>();
			map.put("pageTitle", "Public Timeline");
//			map.put("user", user);
			List<Message> messages = service.getPublicTimelineMessages();
			map.put("messages", messages);
			return new ModelAndView(map, "timeline.ftl");
		}, new FreeMarkerEngine());

		/*
		 * 显示指定用户所发布的所有消息并判断当前用户与指定用户是否是关注关系
		 */
		get("/t/:username", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);

			User authUser = getAuthenticatedUser(req);
			boolean followed = false;
			if (authUser != null) {
				followed = service.isUserFollower(authUser, profileUser);
			}
			List<Message> messages = service.getUserTimelineMessages(profileUser);

			Map<String, Object> map = new HashMap<>();
			map.put("pageTitle", username + "'s Timeline");
			map.put("user", authUser);
			map.put("profileUser", profileUser);
			map.put("followed", followed);
			map.put("messages", messages);
			return new ModelAndView(map, "timeline.ftl");
		}, new FreeMarkerEngine());
		/*
		 * 检查指定用户的用户名是否存在
		 */
		before("/t/:username", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);
			if (profileUser == null) {
				halt(404, "User not Found");
			}
		});

		/*
		 * 关注指定用户
		 */
		get("/auth/t/:username/follow", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);
			User authUser = getAuthenticatedUser(req);

			service.followUser(authUser, profileUser);
			res.redirect("/t/" + username);
			return null;
		});
		/*
		 * 关注指定用户前检查用户是否已经登陆，没有登陆跳转至登陆页面，如果指定用户的用户名 不存在则报错
		 */
		before("/auth/t/:username/follow", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);
			if (profileUser == null) {
				halt(404, "User not Found");
			}
		});

		/*
		 * 与指定用户解除关注关系，相关逻辑与关注指定用户相同
		 */
		get("/auth/t/:username/unfollow", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);
			User authUser = getAuthenticatedUser(req);

			service.unfollowUser(authUser, profileUser);
			res.redirect("/t/" + username);
			return null;
		});
		/*
		 * 检查用户是否已经登陆及指定用户的用户名是否存在
		 */
		before("/auth/t/:username/unfollow", (req, res) -> {
			String username = req.params(":username");
			User profileUser = service.getUserbyUsername(username);
			if (profileUser == null) {
				halt(404, "User not Found");
			}
		});

		/*
		 * 重定向至登陆页面
		 */
		get("/login", (req, res) -> {
			Map<String, Object> map = new HashMap<>();
			if (req.queryParams("r") != null) {
				map.put("message", "You were successfully registered and can login now");
			}
			return new ModelAndView(map, "login.ftl");
		}, new FreeMarkerEngine());
		/*
		 * 处理登陆
		 */
		post("/login", (req, res) -> {

			Map<String, Object> map = new HashMap<>();
			User user = new User();
			try {
				// MultiMap<V> extends HashMap<String,List<V>>，用来存储表单值
				MultiMap<String> params = new MultiMap<String>();
				// 将请求体中的表单数据解析到multiMap中
				UrlEncoded.decodeTo(req.body(), params, "UTF-8");
				// MultiMap的toString()方法已经重载可以打印出其中的键值对
				log.info("登陆表单数据：" + params);
				// 将表单数据填充到user对象中
				BeanUtils.populate(user, params);
			} catch (Exception e) {
				// 如果抛出异常就中断请求处理返回一个错误码
				halt(501);
				return null;
			}
			LoginResult result = service.checkUser(user);
			// 如果账户密码验证正确则将用户信息添加到session中，重定向返回到首页
			// 否则返回登陆首页，给出错误提示
			if (result.getUser() != null) {
				addAuthenticatedUser(req, result.getUser());
				log.info("用户登陆成功，用户信息为：" + result.getUser());
				if (user.getAutoLogin() != null && user.getAutoLogin().equals("true")) {
					log.info("添加cookie实现七天免登陆");
					// 若勾选了七天免登陆，则添加一个包含用户名与密码，有效期为7天的cookie信息,注意result中的用户密码
					// 已经经过散列加密
					res.cookie("autologin", user.getUsername() + "-" + result.getUser().getPassword(),
							60 * 60 * 24 * 7);
				}
				res.redirect("/index");
				halt();
			} else {
				map.put("error", result.getError());
			}
			map.put("username", user.getUsername());
			return new ModelAndView(map, "login.ftl");
		}, new FreeMarkerEngine());
		/*
		 * 检查用户是否已经登陆，若登陆成功则直接挑战至首页
		 */
		// before("/login", (req, res) -> {
		// User authUser = getAuthenticatedUser(req);
		// if(authUser != null) {
		// res.redirect("/index");
		// halt();
		// }
		// });

		/*
		 * 重定向至注册页面
		 */
		get("/register", (req, res) -> {
			Map<String, Object> map = new HashMap<>();
			return new ModelAndView(map, "register.ftl");
		}, new FreeMarkerEngine());
		/*
		 * 执行注册操作
		 */
		post("/register", (req, res) -> {
			Map<String, Object> map = new HashMap<>();
			User user = new User();
			try {
				MultiMap<String> params = new MultiMap<String>();
				UrlEncoded.decodeTo(req.body(), params, "UTF-8");
				log.info("用户注册表单信息：" + params);
				BeanUtils.populate(user, params);
			} catch (Exception e) {
				halt(501);
				return null;
			}
			String error = user.validate();
			if (StringUtils.isEmpty(error)) {
				// 判断该用户名是否存在，若存在则返回至注册页面，给出错误提示
				User existingUser = service.getUserbyUsername(user.getUsername());
				if (existingUser == null) {
					// 注册成功后重定向至登陆页面
					service.registerUser(user);
					res.redirect("/login?r=1");
					halt();
				} else {
					error = "The username is already taken";
				}
			}
			map.put("error", error);
			map.put("username", user.getUsername());
			map.put("email", user.getEmail());
			return new ModelAndView(map, "register.ftl");
		}, new FreeMarkerEngine());
		/*
		 * 检查用户是否已经登陆，若登陆成功则直接挑战至首页
		 */
		// before("/register", (req, res) -> {
		// User authUser = getAuthenticatedUser(req);
		// if(authUser != null) {
		// res.redirect("/index");
		// halt();
		// }
		// });

		/*
		 * 发布消息
		 */
		post("/auth/message", (req, res) -> {
			User user = getAuthenticatedUser(req);
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8");
			Message m = new Message();
			m.setUserId(user.getId());
			m.setPubDate(new Date());
			BeanUtils.populate(m, params);
			service.addMessage(m);
			res.redirect("/index");
			return null;
		});

		/*
		 * 注销
		 */
		get("/logout", (req, res) -> {
			removeAuthenticatedUser(req);
			res.redirect("/public");
			return null;
		});

		// 删除指定id的消息
		get("/deleteMessage/:id", (req, res) -> {
			String id = req.params(":id");
			Map<String, Object> map = new HashMap<>();
			if (StringUtils.isNotEmpty(id)) {
				int messageId = Integer.parseInt(id);
				service.deleteMessage(messageId);
				// map.put("msg", "删除消息成功");
				res.redirect("/index");
			}
			return null;
		});
       
		//根据条件查找消息
		post("/getMessageByCondition", (req, res) -> {
			Map<String, Object> map = new HashMap<>();
			MultiMap<String> params = new MultiMap<>();
			Condition condition = new Condition();
			User user = getAuthenticatedUser(req);
			if (user != null) {
				condition.setUserId(user.getId());
			}
			UrlEncoded.decodeTo(req.body(), params, "UTF-8");
			BeanUtils.populate(condition, params);
			List<Message> messages = service.getMessageByCondition(condition);
			map.put("pageTitle", "Public Timeline");
			map.put("user", user);
			map.put("messages", messages);
			return new ModelAndView(map, "timeline.ftl");
		},new FreeMarkerEngine());
		
		//更新消息
		post("/updateMessage", (req,res)->{
			User user = getAuthenticatedUser(req);
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8");
			Message m = new Message();
			m.setUserId(user.getId());
			m.setPubDate(new Date());
			BeanUtils.populate(m, params);
			service.updateMessage(m);;
			res.redirect("/index");
			return null;
		});
		
		//跳转至更新消息页面
		get("/updateMessage/:id",(req,res)->{
               String id=req.params(":id");
               User user=getAuthenticatedUser(req);
               Map<String, Object> map=new HashMap<>();
               if(StringUtils.isNotEmpty(id)){
            	   int messageId=Integer.parseInt(id);
            	   Message message=service.getMessageById(messageId);
            	   map.put("message", message);
            	   map.put("user", user);
            	   map.put("pageTitle", "Update Message");
            	   System.out.println("跳转至更新页面");
            	   return new ModelAndView(map, "updateMessage.ftl");
               }
               halt(404,"please input correct messageId");
               return null;
		} ,new FreeMarkerEngine());
	}

	/**
	 * 将user对象添加到session中
	 * 
	 * @param request
	 * @param u
	 */
	private void addAuthenticatedUser(Request request, User u) {
		request.session().attribute(USER_SESSION_ID, u);

	}

	/**
	 * 从session中移除user对象
	 * 
	 * @param request
	 */
	private void removeAuthenticatedUser(Request request) {
		request.session().removeAttribute(USER_SESSION_ID);

	}

	/**
	 * 判断用户是否登陆，若登陆返回user对象
	 * 
	 * @param request
	 * @return
	 */
	private User getAuthenticatedUser(Request request) {
		return request.session().attribute(USER_SESSION_ID);
	}

}
