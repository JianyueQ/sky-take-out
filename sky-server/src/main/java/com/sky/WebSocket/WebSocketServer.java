package com.sky.WebSocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap();

    /**
     * 连接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("与客户端:"+sid+":建立连接");
        sessionMap.put( sid , session );
    }

    /**
     * 收到客户端消息调用的方法
     */
    @OnMessage
    public void onMessage(String message,@PathParam("sid") String sid) {
        System.out.println("收到来自客户端"+sid+"的消息:"+message);
    }

    /**
     * 与客户端断开连接调用的方法
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("与客户端"+sid+"断开连接");
        sessionMap.remove( sid );
    }

    /**
     * 群发消息
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            //服务器向客户端发送消息
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
