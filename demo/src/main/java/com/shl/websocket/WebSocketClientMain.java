package com.shl.websocket;

import java.net.URI;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class WebSocketClientMain {

	public static void main(String[] args) {
		String dest = "ws://localhost:8080/jsr356toUpper";
		WebSocketClient client = new WebSocketClient();
		try {
			
			ToUpperClientSocket socket = new ToUpperClientSocket();
			client.start();
			URI echoUri = new URI(dest);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			socket.getLatch().await();
			socket.sendMessage("echo");
			socket.sendMessage("test");
			Thread.sleep(10000l);

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
/*
 * 服务器端：
 * WebSocket opened: 127.0.0.1:8080->127.0.0.1:51906
Message received: echo
Message received: test
Closing a WebSocket due to Shutdown
客户端：
Connected to server
Message received from server:ECHO
Message received from server:TEST

 */
}
