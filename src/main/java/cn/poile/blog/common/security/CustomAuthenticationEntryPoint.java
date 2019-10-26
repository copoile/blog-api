package cn.poile.blog.common.security;

import cn.poile.blog.common.response.ApiResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: yaohw
 * @create: 2019-10-25 15:16
 **/
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

     private CustomAuthenticationExceptionHandle authenticationExceptionHandle = new CustomAuthenticationExceptionHandle();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print(JSON.toJSON(authenticationExceptionHandle.handle(authException)));
    }
}
