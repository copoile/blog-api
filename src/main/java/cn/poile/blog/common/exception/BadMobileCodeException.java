package cn.poile.blog.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 手机号验证码认证 验证码不正确异常
 * @author: yaohw
 * @create: 2019-11-07 16:58
 **/
public class BadMobileCodeException extends AuthenticationException {

    public BadMobileCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public BadMobileCodeException(String msg) {
        super(msg);
    }
}
