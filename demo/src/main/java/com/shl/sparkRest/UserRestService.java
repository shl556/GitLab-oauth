package com.shl.sparkRest;

import static spark.Spark.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import sun.awt.RepaintArea;

public class UserRestService {
    
	public static void main(String[] args) {
		
		exception(Exception.class, (e, req, res) -> {
			res.status(405);
			res.body("对不起，服务器内部错误");
		});
		
		
		
		User user=new User(1,"shl", "北京", 24, "123456");
		User user2=new User(2,"shl2", "北京", 24, "123456");
		User user3=new User(3,"shl3", "北京", 24, "123456");
		User user4=new User(4,"shl4", "北京", 24, "123456");
		//查询所有的用户
		get("/users", (req,res)->{
			List<User> users=Arrays.asList(user,user2,user3,user4);
			res.header("Content-Type", "application/json");
			return users;
		},new JsonTransformer());
		
		//查询某个用户
		get("/users/:id", (req,res)->{
			int id =Integer.parseInt(req.params(":id"));
			User result=new User();
			switch (id) {
			case 1:
				result=user;
				break;
			case 2:
				result=user2;
				break;
			case 3:
				result=user3;
				break;
			case 4:
				result=user4;
				break;
			default:
				result.setError(new Error("id为"+id+"的用户不存在","401"));
			}
			res.header("Content-Type", "application/json");
			return result;
		},new JsonTransformer());
		
		//修改用户信息，返回修改后的用户信息
		post("/users", (req,res)->{
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8",10);
			int id=Integer.parseInt(params.get("id").get(0));
			User result=new User();
			List<Integer> allow=Arrays.asList(1,2,3,4);
			res.header("Content-Type", "application/json");
			if(allow.contains(id)){
				BeanUtils.populate(result, params);
			}else{
				result.setError(new Error("id为"+id+"的用户不存在","401"));
			}
			return result;
		}, new JsonTransformer());
		
		//删除用户信息
		delete("/users/:id", (req,res)->{
			int id =Integer.parseInt(req.params(":id"));
			List<Integer> allow=Arrays.asList(1,2,3,4);
			res.header("Content-Type", "application/json");
			User result=new User();
			if(!allow.contains(id)){
				result.setError(new Error("id为"+id+"的用户不存在","401"));
			}
			return result;
		},new JsonTransformer());
		
		//新建一个用户
		put("/users", (req,res)->{
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(req.body(), params, "UTF-8",10);
			User result=new User();
			BeanUtils.populate(result, params);
			List<String> names=Arrays.asList("shl","shl2","shl3","shl4");
			if(names.contains(result.getUserName())){
				result=new User();
				result.setError(new Error("用户名"+result.getUserName()+"已存在", "402"));
			}else{
			result.setId(5);
			}
			res.header("Content-Type", "application/json");
			return result;
		},new JsonTransformer());
	}
}
