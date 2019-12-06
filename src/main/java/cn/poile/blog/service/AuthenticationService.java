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
     * @param clientId 客户端id
     * @return cn.poile.blog.vo.TokenVo
     */
    AuthenticationToken usernameOrMobilePasswordAuthenticate(String s, String password,String clientId);

    /**
     * 手机号验证码认证
     * @param mobile 手机号
     * @param code 验证码
     * @param clientId 客户端id
     * @return
     */
    AuthenticationToken mobileCodeAuthenticate(long mobile,String code,String clientId);

    /**
     * 移除 accessToken 相关
     * @param accessToken accessToken
     * @param clientId 客户端id
     */
    void remove(String accessToken,String clientId);

    /**
     * 刷新 accessToken
     * @param refreshToken refreshToken
     * @param clientId 客户端id
     * @return
     */
    AuthenticationToken refreshAccessToken(String refreshToken,String clientId);
}
