package cn.poile.blog.common.security;

import cn.poile.blog.common.response.ApiResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static cn.poile.blog.common.constant.ErrorEnum.*;

/**
 * 自定义认证失败
 *
 * @author: yaohw
 * @create: 2019-10-25 15:16
 **/
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(JSON.toJSON(customAuthenticationExceptionHandle(authException)));
    }


    /**
     * AuthenticationException 处理
     *
     * @param ex 认证异常
     * @return
     */
    private ApiResponse customAuthenticationExceptionHandle(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return new ApiResponse(BAD_CREDENTIALS.getErrorCode(),BAD_CREDENTIALS.getErrorMsg());
        } else if (ex instanceof DisabledException) {
            return new ApiResponse(ACCOUNT_DISABLE.getErrorCode(),ACCOUNT_DISABLE.getErrorMsg());
        } else if (ex instanceof AccountExpiredException) {
            return new ApiResponse(ACCOUNT_EXPIRED.getErrorCode(),ACCOUNT_EXPIRED.getErrorMsg());
        } else if (ex instanceof LockedException) {
            return new ApiResponse(ACCOUNT_LOCKED.getErrorCode(),ACCOUNT_LOCKED.getErrorMsg());
        } else if (ex instanceof CredentialsExpiredException) {
            return new ApiResponse(CREDENTIALS_EXPIRED.getErrorCode(),CREDENTIALS_EXPIRED.getErrorMsg());
        } else if (ex instanceof InsufficientAuthenticationException) {
            return new ApiResponse(ACCESS_DENIED.getErrorCode(),ACCESS_DENIED.getErrorMsg());
        }
        return new ApiResponse(SYSTEM_ERROR.getErrorCode(),SYSTEM_ERROR.getErrorMsg());
    }
}
