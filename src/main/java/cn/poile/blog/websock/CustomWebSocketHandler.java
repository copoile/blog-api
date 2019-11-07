package cn.poile.blog.websock;

import cn.poile.blog.vo.CustomUserDetails;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * websock 处理器
 * @author: yaohw
 * @create: 2019-11-07 11:27
 **/
@Component
@Log4j2
public class CustomWebSocketHandler implements WebSocketHandler {

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    private static ArrayList<WebSocketSession> sessions = new ArrayList<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        sessions.add(webSocketSession);
        onlineCount.incrementAndGet();
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 指定用户发送消息
     * @param userId
     * @param webSocketMessageDTO
     */
    public void sendMessage(Integer userId, WebSocketMessageDTO webSocketMessageDTO) {
        if (userId == null) {
            sendMessage(webSocketMessageDTO);
        } else {
            sessions.forEach(session -> {
                Map<String, Object> attributes = session.getAttributes();
                CustomUserDetails userDetails = (CustomUserDetails) attributes.get("user");
                if (session.isOpen() && userId.equals(userDetails.getId())) {
                    try {
                        TextMessage textMessage = new TextMessage(JSON.toJSONString(webSocketMessageDTO));
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.error("webSocket 发送消息失败:{0}", e);
                    }
                }
            });
        }
    }

    /**
     * 广播在线客户端
     * @param webSocketMessageDTO
     */
    public void sendMessage(WebSocketMessageDTO webSocketMessageDTO) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    TextMessage textMessage = new TextMessage(JSON.toJSONString(webSocketMessageDTO));
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("webSocket 发送消息失败:{0}", e);
                }
            }
        });
    }
}
