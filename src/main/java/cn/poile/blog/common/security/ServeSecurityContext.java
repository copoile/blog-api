package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.vo.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 服务安全上下文
 *
 * @author: yaohw
 * @create: 2019-11-03 15:00
 **/
public class ServeSecurityContext {
    /**
     * 获取当前用户相信信息
     *
     * @return
     */
    public static CustomUserDetails getUserDetail(boolean throwEx) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null && throwEx) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal == null && throwEx) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        return (CustomUserDetails) principal;
    }

    /**
     * 获取认证信息
     *
     * @return org.springframework.security.core.Authentication
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    /**
     * 获取AuthenticationToken
     *
     * @return cn.poile.blog.common.security.AuthenticationToken
     */
    public static AuthenticationToken getAuthenticationToken(boolean throwEx) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null && throwEx) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details == null && throwEx) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        return (AuthenticationToken) details;
    }

}
