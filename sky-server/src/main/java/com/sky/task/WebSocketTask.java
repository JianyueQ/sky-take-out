package com.sky.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.WebSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketTask {
    @Autowired
    private WebSocketServer webSocketServer;

    @Scheduled(cron = "0/10 * * * * ?")
    public void sendMessageToClient() throws JsonProcessingException {
//        // 创建JSON对象
//        Map<String, String> message = new HashMap<>();
//        message.put("content", "这是来自于服务端的消息");
//        message.put("time", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
//
//        // 转换为JSON字符串
//        String jsonMessage = new ObjectMapper().writeValueAsString(message);
        webSocketServer.sendToAllClient("这是来自于服务端的消息:"+ DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
//        webSocketServer.sendToAllClient(jsonMessage);
    }

}


