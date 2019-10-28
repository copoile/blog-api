package cn.poile.blog.service.impl;

import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.security.Token;
import cn.poile.blog.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author: yaohw
 * @create: 2019-10-28 18:27
 **/
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTokenStore tokenStore;

    /**
     * 用户名或手机号密码认证
     *
     * @param s 手机号或用户名
     * @param password 密码
     * @return cn.poile.blog.vo.TokenVo
     */
    @Override
    public Token usernameOrMobilePasswordAuthenticate(String s, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(s,password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return tokenStore.storeAccessToken(authenticate);
    }
}
