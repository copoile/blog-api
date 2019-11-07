package cn.poile.blog.service.impl;

import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.MobileCodeAuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.sms.SmsCodeService;
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

    @Autowired
    private SmsCodeService smsCodeService;

    /**
     * 用户名或手机号密码认证
     * @param s  手机号或用户名
     * @param password 密码
     * @return cn.poile.blog.vo.TokenVo
     */
    @Override
    public AuthenticationToken usernameOrMobilePasswordAuthenticate(String s, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(s, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return tokenStore.storeAccessToken(authenticate);
    }

    /**
     * 手机号验证码认证
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public AuthenticationToken mobileCodeAuthenticate(long mobile, String code) {
        MobileCodeAuthenticationToken authenticationToken = new MobileCodeAuthenticationToken(mobile, code);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        AuthenticationToken storeAccessToken = tokenStore.storeAccessToken(authenticate);
        smsCodeService.deleteSmsCode(mobile);
        return storeAccessToken;
    }

    /**
     * 移除 accessToken 相关
     * @param accessToken
     */
    @Override
    public void remove(String accessToken) {
        tokenStore.remove(accessToken);
    }

    /**
     * 刷新accessToken
     * @param refreshToken
     * @return
     */
    @Override
    public AuthenticationToken refreshAccessToken(String refreshToken) {
        return tokenStore.refreshAccessToken(refreshToken);
    }


}
