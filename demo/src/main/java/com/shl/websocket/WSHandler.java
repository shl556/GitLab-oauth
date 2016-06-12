package com.shl.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebSocket
public class WSHandler extends WebSocketHandler {
 
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    }
 
    @OnWebSocketError
    public void onError(Throwable t) {
    }
 
    @OnWebSocketConnect
    public void onConnect(Session session) {
    }
 
    @OnWebSocketMessage
    public void onMessage(String message) {
    }
 
    @Override
    public void configure(WebSocketServletFactory factory) {
        // TODO Auto-generated method stub
        factory.register(WSHandler.class);
    }
    public static void main(String[] args) throws Exception {
    	Server server = new Server(2014);
    	 server.setHandler(new WSHandler());
    	   server.setStopTimeout(0);
    	    server.start();
    	    server.join();
	}
}