package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * @param e 认证异常
     * @return cn.poile.blog.common.response.ApiResponse
     */
    private ApiResponse customAuthenticationExceptionHandle(AuthenticationException e) {
        ApiResponse response = new ApiResponse();
        if (e instanceof BadCredentialsException) {
            response.setErrorCode(ErrorEnum.BAD_CREDENTIALS.getErrorCode());
            response.setErrorMsg(ErrorEnum.BAD_CREDENTIALS.getErrorMsg());
            return response;
        } else if (e instanceof DisabledException) {
            response.setErrorCode(ErrorEnum.ACCOUNT_DISABLE.getErrorCode());
            response.setErrorMsg(ErrorEnum.ACCOUNT_DISABLE.getErrorMsg());
            return response;
        } else if (e instanceof AccountExpiredException) {
            response.setErrorCode(ErrorEnum.ACCOUNT_EXPIRED.getErrorCode());
            response.setErrorMsg(ErrorEnum.ACCOUNT_EXPIRED.getErrorMsg());
            return response;
        } else if (e instanceof LockedException) {
            response.setErrorCode(ErrorEnum.ACCOUNT_LOCKED.getErrorCode());
            response.setErrorMsg(ErrorEnum.ACCOUNT_LOCKED.getErrorMsg());
            return response;
        } else if (e instanceof CredentialsExpiredException) {
            response.setErrorCode(ErrorEnum.CREDENTIALS_EXPIRED.getErrorCode());
            response.setErrorMsg(ErrorEnum.CREDENTIALS_EXPIRED.getErrorMsg());
            return response;
        } else if (e instanceof InsufficientAuthenticationException) {
            response.setErrorCode(ErrorEnum.ACCESS_DENIED.getErrorCode());
            response.setErrorMsg(ErrorEnum.ACCESS_DENIED.getErrorMsg());
            return response;
        }
        return response;
    }
}
