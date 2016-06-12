package com.spark.websocket;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;
import java.text.*;
import java.util.*;
import static spark.Spark.*;

public  class Chat {

    static Map<Session, String> userUsernameMap = new HashMap<>();
    static int nextUserNumber = 1; 
    
    public static void main(String[] args) {
    	//设置静态资源文件位置，位于类路径下的pulic文件夹
        staticFileLocation("/public"); 
        webSocket("/chat", ChatWebSocketHandler.class);
        System.out.println("启动服务器");
        init();
    }

    //发送消息，将消息和用户列表转换成json字符串发送
    public static void broadcastMessage(String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
           try {
               session.getRemote().sendString(String.valueOf(new JSONObject()
                   .put("userMessage", message)
                   .put("userlist", userUsernameMap.values())
               ));
           } catch (Exception e) {
               e.printStackTrace();
           }
       });
   }

}
