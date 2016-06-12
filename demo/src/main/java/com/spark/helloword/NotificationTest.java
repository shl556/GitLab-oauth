package com.spark.helloword;
import static spark.Spark.*;
public class NotificationTest {
     public static void main(String[] args) {
		  post("/buildInfo", (req,res)->{
			     String info=req.body();
			     System.out.println("发送过来的状态信息："+info);
			     return null;
		  });
		  before((req,res)->{
			   String url=req.uri();
			   System.out.println("请求路径："+url);
		  });
	}
}
