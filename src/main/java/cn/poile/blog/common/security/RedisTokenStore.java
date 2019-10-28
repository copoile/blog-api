package cn.poile.blog.common.security;

import cn.poile.blog.vo.TokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019-10-28 17:48
 **/
@Service
public class RedisTokenStore {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String AUTH_ACCESS_TOKEN = "auth:accessToken:";

    private static final String AUTH_REFRESH_TOKEN = "auth:refreshToken";

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 43200L;

    private static final long REFRESH_TOKENV_ALIDITY_SECONDS = 2592000L;

    private String prefix = "";

    /**
     *  存 accessToken
     * @param accessToken
     * @param token
     * @return void
     */
    public void storeAccessToken(String accessToken, TokenVo token) {
        redisTemplate.opsForValue().set(prefix + AUTH_ACCESS_TOKEN + accessToken,token, ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
    }

    /**
     *  读 accessToken
     * @param accessToken
     * @return cn.poile.blog.vo.TokenVo
     */
    public TokenVo readAccessToken(String accessToken) {
        return (TokenVo)redisTemplate.opsForValue().get(prefix + AUTH_ACCESS_TOKEN + accessToken);
    }

    /**
     *  存 refreshToken
     * @param refreshToken
     * @param token
     * @return void
     */
    public void storeRefreshToken(String refreshToken, TokenVo token) {
        redisTemplate.opsForValue().set(prefix + AUTH_REFRESH_TOKEN + refreshToken,token,REFRESH_TOKENV_ALIDITY_SECONDS,TimeUnit.SECONDS);
    }
    /**
     *  读 refreshToken
     * @param refreshToken
     * @return cn.poile.blog.vo.TokenVo
     */
    public TokenVo readRefreshToken(String refreshToken) {
        return (TokenVo)redisTemplate.opsForValue().get(prefix + AUTH_REFRESH_TOKEN + refreshToken);
    }


}
