package com.shl.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(urlPatterns="/toUpper")
//通过该servlet配置websocket类的映射路径
public class ToUpperWebSocketServlet  extends WebSocketServlet{

	@Override
	public void configure(WebSocketServletFactory factory) {
		
	      factory.register(ToUpperWebSocket.class);
	     //当连接在10s内未活动则关闭连接,getPolicy()方法获取服务器端的配置策略，可以设定
	      //发送文本或者二进制消息的大小和缓冲区大小等等
	      factory.getPolicy().setIdleTimeout(10000);
		
	}
	@Override
	public void doGet(HttpServletRequest request,
	        HttpServletResponse response) throws ServletException, IOException {
	    //对get请求作出正确回应，避免服务器发送404错误码
		response.getWriter().println("HTTP GET method not implemented.");
	}
}