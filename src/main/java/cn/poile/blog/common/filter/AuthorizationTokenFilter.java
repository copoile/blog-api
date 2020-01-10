package cn.poile.blog.common.filter;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationToken 校验过滤器
 * @author: yaohw
 * @create: 2019-10-29 19:47
 **/
@Component
public class AuthorizationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTokenStore tokenStore;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String TOKEN_TYPE = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(TOKEN_TYPE)) {
           String accessToken = authorization.substring(7);
           if (!accessToken.isEmpty()) {
               AuthenticationToken cacheAuthenticationToken = tokenStore.readByAccessToken(accessToken);
               if (cacheAuthenticationToken == null) {
                   httpServletResponse.setCharacterEncoding("UTF-8");
                   httpServletResponse.setContentType("application/json; charset=utf-8");
                   httpServletResponse.getWriter().print(JSON.toJSON(createErrorResponse()));
                   return;
               }
               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(cacheAuthenticationToken.getPrincipal(), null, cacheAuthenticationToken.getPrincipal().getAuthorities());
               authentication.setDetails(cacheAuthenticationToken);
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private ApiResponse createErrorResponse() {
        ApiResponse response = new ApiResponse();
        response.setCode(ErrorEnum.CREDENTIALS_INVALID.getErrorCode());
        response.setMessage(ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        return response;
    }
}
