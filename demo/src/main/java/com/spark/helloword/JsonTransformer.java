package com.spark.helloword;

import org.json.JSONObject;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
	@Override
	public String render(Object model) throws Exception {
		JSONObject obj=new JSONObject(model);
		return obj.toString();
	}
   public static void main(String[] args) {
	    JsonTransformer json=new JsonTransformer();
	    User user=new User("shl","北京", 25, "123456");
	    try {
			System.out.println(json.render(user));
			String  s=json.render(user);
			JSONObject obj=new JSONObject(s);
			System.out.println(obj.get("userName"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
