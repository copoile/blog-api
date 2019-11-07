package cn.poile.blog.websock;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.websocket.server.ServerEndpoint;

/**
 * @author: yaohw
 * @create: 2019-11-07 11:29
 **/
@Configuration
@EnableWebSocket
@Log4j2
@ServerEndpoint("/websocket")
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private CustomWebSocketHandler webSocketHandler;

    @Autowired
    private CustomHandShakeInterceptor handShakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler,"/websocket").addInterceptors(handShakeInterceptor).setAllowedOrigins("*");
    }
}
