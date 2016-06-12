package com.spark.websocket;

import static j2html.TagCreator.*;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;


import  sun.misc.BASE64Decoder;

@WebSocket
public class ChatWebSocketHandler {


    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Chat.nextUserNumber++;
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(createHtmlMessageFromSender("Server", username+"join the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(createHtmlMessageFromSender("Server", username+"leave the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	//图片的二进制数据经过base64编码后的头部信息
    	String header ="data:image/jpeg;base64,";
        //普通文本
    	if(!message.contains(header)){
          Chat.broadcastMessage(createHtmlMessageFromSender(getUserName(user), message));
        }else{
    	//图片数据
    	try {
    		String img = message.substring(header.length());
    		BASE64Decoder decoder = new BASE64Decoder();
    		byte[] bytes=decoder.decodeBuffer(img);
    		System.out.println(bytes.length);
		    String imageFileName=Chat.userUsernameMap.get(user)+System.currentTimeMillis();
			FileOutputStream out=new FileOutputStream("e://"+imageFileName+".jpg");
			out.write(bytes);    
			out.close();
			System.out.println("接受图片，存储为："+imageFileName);
            Chat.broadcastMessage(createImageFromSender(getUserName(user), message));
    	} catch (Exception e) {
            throw new RuntimeException(e);
		}
     }
  }
    //返回包含文本信息的HTML片段
    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
    //返回包含图片信息的HTML片段
    private String createImageFromSender(String sender, String message) {
    	return article().with(
    		    div().with(b(sender + " says:"),span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))),
    			img().attr("src", message).withClass("chatImage")
    			).render();
    }

    private String getUserName(Session user){
        return  Chat.userUsernameMap.get(user);
    }
    
}
