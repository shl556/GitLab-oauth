package com.spark.helloword;

import static spark.Spark.*;

import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import spark.QueryParamsMap;
import spark.Session;
public  class HelloWorld {
	public void test2(){
		//开启微服务，第一个参数是url，第二个参数是响应内容
    	//除非显示调用stop方法或者关闭虚拟机否则微服务会一直运行
    	//按照声明的先后顺序依次匹配，第一个匹配的会被优先调用
        get("/hello", (req, res) -> "<h1>Hello World1</h1>");
        get("/hello", (req, res) -> "<h1>Hello World3</h1>");
        get("/hello/:name", (req, res) -> "<h1>Hello World3</h1>"+req.params(":name"));
//        get("/hello/*/test/*", (req, res) -> "<h1>Hello World3</h1>"+req.splat().length);
        get("/hello/*/test/*/*/*", (req, res) ->{
        	 	String[] splats=req.splat();
        	 	String result="";
        	 	for(String splat:splats){
        	 		result+=splat;
        	 	}
        	 	return result+splats.length;
        });
        
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //停止微服务，清除所有的路由
        System.out.println("停止微服务");
//        stop();
    
	}
	public void test3(){
		 before((req,res)->{
			   res.cookie("userName", "shl",6000);
			   res.cookie("password", "123456",6000);
			   System.out.println("请求处理前操作1");
		 });
		 before((req,res)->{
			 System.out.println("请求处理前操作2");
			 halt(404,"请求中止");
		 });
		 before((req,res)->{
			 System.out.println("请求处理前操作3");
		 });
		 after((req,res)->{
			 System.out.println("请求处理后操作3");
		 });
		 after((req,res)->{
			 System.out.println("请求处理后操作2");
		 });
		 after((req,res)->{
			 System.out.println("请求处理后操作1");
			 res.removeCookie("userName");
			 res.removeCookie("password");
		 });
		  get("/filterTest",(req,res)->{
			   System.out.println("请求处理中");
			    return req.cookie("userName")+" "+req.cookie("password");
		  });
		  post("/register/:param1/:param2", (req,res)->{
			   System.out.println("req.body():"+req.body());
			   System.out.println("contentLength:"+req.contentLength());
			   System.out.println("contentType:"+req.contentType());
			   System.out.println("contextPath:"+req.contextPath());
			   //host:localhost:4567
			   System.out.println("host:"+req.host());
			   System.out.println("ip:"+req.ip());
			   //pathInfo:/register/test/test
			   System.out.println("pathInfo:"+req.pathInfo());
			   System.out.println("port:"+req.port());
			  //请求协议protocol:HTTP/1.1
			   System.out.println("protocol:"+req.protocol());
			   //请求查询字符串,queryString:user[name]=sun&password=123456
			   System.out.println("queryString:"+req.queryString());
			   System.out.println("requestMethod:"+req.requestMethod());
			   //请求协议,scheme:http
			   System.out.println("scheme:"+req.scheme());
			   System.out.println("servletPath:"+req.servletPath());
			   //uri:/register/test/test
			   System.out.println("uri:"+req.uri());
			   //url:http://localhost:4567/register/test/test
			   System.out.println("url:"+req.url());
			   //中止处理请求，返回一个错误码和指定的响应内容
//			   halt(401, "请求地址有误");
			   //客户端
			   System.out.println("userAgent:"+req.userAgent());
			   Set<String> headers=req.headers();
			   System.out.println("headers:");
			   headers.forEach(s->{
				System.out.println(s+": "+req.headers(s));   
			   });
			   Set<String> attributes=req.attributes();
			   System.out.println("attributes:");
			   attributes.forEach(s->{System.out.println(s);});
			   //rest格式的请求参数
			   Map<String, String> params=req.params();
			   System.out.println("params:");
			   //:param1 :test
			   //:param2 :test
			   params.forEach((s,r)->{System.out.println(s+" :"+r);});
			   Set<String> queryParams=req.queryParams();
			   //请求字符串中的请求参数
			   System.out.println("queryParams:");
			   //user[name]: sun
			   //password: 123456
			   queryParams.forEach(s->{System.out.println(s+": "+req.queryParams(s));});
			   QueryParamsMap map= req.queryMap();
//			   String[]  s=null;
//			   if(map!=null){
//			   s=map.values();
//			   }
//			   System.out.println("queryMaps:");
//			  if(s!=null){
//			   for(String st:s){
//				   System.out.println(st);
//			   }
//			  }
			   //false
			   System.out.println(req.queryMap("user").get("email").hasValue());
			   //获取请求字符串中user[name]的字符串的值,sun
			   System.out.println(req.queryMap("user").get("name").value());
//			   System.out.println(req.queryMap("user").get("address").value());
//			   System.out.println("name:"+map.get("name").value());
//			   System.out.println("address:"+map.get("address").value());
			   HttpServletRequest request=req.raw();
			   System.out.println(request.getAttribute("myage"));
			   System.out.println(request.getParameter("name"));
			   System.out.println("================================");
			   
			   Session session=req.session(true);
			   session.attribute("session1", "test");
			   session.attribute("session2", "test");
			   res.redirect("http://localhost:4567/redictTest");
			   return "test";
			   });
		  get("/redictTest", (req,res)->{
			  Map<String, String> map=req.cookies();
			  map.forEach((s,r)->{System.out.println(s+" :"+r);});
			  Session session=req.session();
			  System.out.println((String)session.attribute("session1"));
			  System.out.println((String)session.attribute("session2"));
			  return "userName:"+req.cookie("userName");
		  });
	}
	
	public void test4(){
		get("/throwexception", (request, response) -> {
		    throw new SQLException();
		});
       //定义全局的异常处理,匹配时优先按照异常类型精确匹配，否则匹配父类型异常
		exception(Exception.class, (e, request, response) -> {
		    response.status(404);
		    response.body("父类型的异常"+e.getMessage()+e.getClass());
		});
		exception(NoSuchFieldException.class, (e, request, response) -> {
			response.status(404);
			response.body("非同一类型的异常"+e.getMessage()+e.getClass());
		});
		exception(SQLTransientException.class, (e, request, response) -> {
			response.status(404);
			response.body("子类型的异常"+e.getMessage()+e.getClass());
		});
		exception(SQLException.class, (e, request, response) -> {
			response.status(404);
			response.body("Resour"+e.getMessage()+e.getClass());
		});
      
	}
	public void test5(){
		     //指定类路径下的静态资源文件位置，如css文件
//		    staticFileLocation("/public");
		//指定返回的响应类型和结果转换器
		get("/hello", "application/json", (request, response) -> {
		    return new User("shl","北京", 25, "123456");
		}, new JsonTransformer());
	}
	public void test6(){
		
	}
	public void test7(){
		
	}
	public void test8(){
		
	}
	public void test(){
		
	}
    public static void main(String[] args) throws Exception {
    	 new HelloWorld().test3();
    }
}
