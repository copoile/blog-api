package cn.poile.blog.service;

import cn.poile.blog.common.security.Token;

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
    Token usernameOrMobilePasswordAuthenticate(String s, String password);
}
