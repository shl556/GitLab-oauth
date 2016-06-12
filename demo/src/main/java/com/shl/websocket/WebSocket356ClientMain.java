package com.shl.websocket;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

public class WebSocket356ClientMain {

	public static void main(String[] args) {
	
		try {

			String dest = "ws://localhost:8080/jsr356toUpper";
			ToUpper356ClientSocket socket = new ToUpper356ClientSocket();
			//获取一个启动容器
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(socket, new URI(dest));

			socket.getLatch().await();
			socket.sendMessage("echo356");
			socket.sendMessage("test356");
			Thread.sleep(10000l);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
/*
 * 客户端：
 * Connected to server
Message received from server:ECHO356
Message received from server:TEST356

服务器端：
[INFO] Started Jetty Server
WebSocket opened: 127.0.0.1:8080->127.0.0.1:51595
Message received: echo356
Message received: test356
Closing a WebSocket due to WebSocket Read EOF
 */
}