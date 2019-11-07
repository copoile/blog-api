package cn.poile.blog.websock;

import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * websock 拦截器，校验身份
 * @author: yaohw
 * @create: 2019-11-07 11:34
 **/
@Component
public class CustomHandShakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Autowired
    private RedisTokenStore tokenStore;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String accessToken = ((ServletServerHttpRequest) request).getServletRequest().getParameter("accessToken");
        AuthenticationToken authenticationToken = tokenStore.readAccessToken(accessToken);
        if (authenticationToken == null) {
            return false;
        }
        attributes.put("user",authenticationToken.getPrincipal());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
