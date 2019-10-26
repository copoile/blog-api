package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;

/**
 * @author: yaohw
 * @create: 2019-10-25 17:12
 **/
public class CustomAuthenticationExceptionHandle {

    /**
     *  AuthenticationException 处理
     * @param e
     * @return cn.poile.blog.common.response.ApiResponse
     */
    public ApiResponse handle(AuthenticationException e) {
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
