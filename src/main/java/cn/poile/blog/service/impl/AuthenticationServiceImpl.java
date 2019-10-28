package cn.poile.blog.service.impl;

import cn.poile.blog.service.AuthenticationService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.TokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * @author: yaohw
 * @create: 2019-10-28 18:27
 **/
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户名或手机号密码认证
     *
     * @param s 手机号或用户名
     * @param password 密码
     * @return cn.poile.blog.vo.TokenVo
     */
    @Override
    public TokenVo usernameOrMobilePasswordAuthenticate(String s, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(s,password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        return null;
    }
}
