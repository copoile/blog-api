package cn.poile.blog.service;

import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.entity.Client;

/**
 * @author: yaohw
 * @create: 2019-10-28 17:51
 **/
public interface AuthenticationService {
    /**
     *  用户名或手机号密码认证
     * @param s 手机号或用户名
     * @param password 密码
     * @param client 客户端
     * @return cn.poile.blog.vo.TokenVo
     */
    AuthenticationToken usernameOrMobilePasswordAuthenticate(String s, String password,Client client);

    /**
     * 手机号验证码认证
     * @param mobile 手机号
     * @param code 验证码
     * @param client 客户端
     * @return
     */
    AuthenticationToken mobileCodeAuthenticate(long mobile,String code,Client client);

    /**
     * 移除 accessToken 相关
     * @param accessToken accessToken
     * @param client 客户端
     */
    void remove(String accessToken,Client client);

    /**
     * 刷新 accessToken
     * @param refreshToken refreshToken
     * @param client 客户端
     * @return
     */
    AuthenticationToken refreshAccessToken(String refreshToken, Client client);
}
