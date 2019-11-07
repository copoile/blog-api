package cn.poile.blog.service;

import cn.poile.blog.common.security.AuthenticationToken;

/**
 * @author: yaohw
 * @create: 2019-10-28 17:51
 **/
public interface AuthenticationService {
    /**
     *  用户名或手机号密码认证
     * @param s 手机号或用户名
     * @param password 密码
     * @return cn.poile.blog.vo.TokenVo
     */
    AuthenticationToken usernameOrMobilePasswordAuthenticate(String s, String password);

    /**
     * 手机号验证码认证
     * @param mobile
     * @param code
     * @return
     */
    AuthenticationToken mobileCodeAuthenticate(long mobile,String code);

    /**
     * 移除 accessToken 相关
     * @param accessToken
     */
    void remove(String accessToken);

    /**
     * 刷新 accessToken
     * @param refreshToken
     * @return
     */
    AuthenticationToken refreshAccessToken(String refreshToken);
}
