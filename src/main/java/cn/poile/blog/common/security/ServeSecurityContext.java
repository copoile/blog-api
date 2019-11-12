package cn.poile.blog.common.security;

import cn.poile.blog.vo.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

/**
 * 服务安全上下文
 * @author: yaohw
 * @create: 2019-11-03 15:00
 **/
public class ServeSecurityContext {
    /**
     * 获取当前用户相信信息
     * @return
     */
    public static CustomUserDetails getUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }
       return (CustomUserDetails)authentication.getPrincipal();
    }

    /**
     * 获取认证信息
     * @return org.springframework.security.core.Authentication
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    /**
     * 获取AuthenticationToken
     * @return cn.poile.blog.common.security.AuthenticationToken
     */
    public static AuthenticationToken getAuthenticationToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (AuthenticationToken)authentication.getDetails();
    }

}
