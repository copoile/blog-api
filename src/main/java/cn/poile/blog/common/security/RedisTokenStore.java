package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.vo.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * accessToken 生成、存储
 * @author: yaohw
 * @create: 2019-10-28 17:48
 **/
@Log4j2
@Component
public class RedisTokenStore {

    /**
     * accessToken key前缀
     */
    private static final String AUTH_ACCESS_TOKEN = "auth:accessToken:";

    /**
     * refreshToken key前缀
     */
    private static final String AUTH_REFRESH_TOKEN = "auth:refreshToken:";

    /**
     * accessToken : refreshToken
     */
    private static final String AUTH_ACCESS = "auth:access:";

    /**
     * accessToken 时效
     */
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 43200L;

    /**
     * accessToken 时效
     */
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 2592000L;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存 accessToken
     *
     * @param authentication
     * @return void
     */
    public AuthenticationToken storeAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = createAccessToken();
        String refreshToken = createRefreshToken();
        AuthenticationToken token = new AuthenticationToken();
        token.setAccessToken(accessToken);
        token.setExpire(ACCESS_TOKEN_VALIDITY_SECONDS);
        token.setRefreshToken(refreshToken);
        token.setPrincipal(customUserDetails);
        Map<String, Object> map = new HashMap<>(3);
        map.put(AUTH_ACCESS_TOKEN + accessToken, token);
        map.put(AUTH_REFRESH_TOKEN + refreshToken, token);
        map.put(AUTH_ACCESS + accessToken,refreshToken);
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.expire(AUTH_ACCESS_TOKEN + accessToken, ACCESS_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        redisTemplate.expire(AUTH_REFRESH_TOKEN + refreshToken, REFRESH_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS);
        redisTemplate.expire(AUTH_ACCESS + accessToken,REFRESH_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
        return token;
    }

    /**
     * 读 accessToken
     *
     * @param accessToken
     * @return AuthenticationToken
     */
    public AuthenticationToken readAccessToken(String accessToken) {
        return (AuthenticationToken) redisTemplate.opsForValue().get(AUTH_ACCESS_TOKEN + accessToken);
    }

    /**
     * 读 refreshToken
     *
     * @param refreshToken
     * @return AuthenticationToken
     */
    public AuthenticationToken readRefreshToken(String refreshToken) {
        return (AuthenticationToken) redisTemplate.opsForValue().get(AUTH_REFRESH_TOKEN + refreshToken);
    }

    /**
     * 刷新 accessToken
     * @param refreshToken
     * @return
     */
    public AuthenticationToken refreshAccessToken(String refreshToken) {
        AuthenticationToken authenticationToken = readRefreshToken(refreshToken);
        if (authenticationToken == null) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(),ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        String key = authenticationToken.getAccessToken();
        redisTemplate.opsForValue().set(AUTH_ACCESS_TOKEN + key, authenticationToken,ACCESS_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
        return authenticationToken;
    }

    /**
     * accessToken 过期时间
     * @param accessToken
     * @return
     */
    private long accessTokenExpire(String accessToken) {
        return redisTemplate.getExpire(AUTH_ACCESS_TOKEN  + accessToken,TimeUnit.SECONDS);
    }

    /**
     * refreshToken 过期时间
     * @param refreshToken
     * @return
     */
    private long readRefreshTokenExpire(String refreshToken) {
        return redisTemplate.getExpire(AUTH_REFRESH_TOKEN + refreshToken,TimeUnit.SECONDS);
    }


    /**
     * 移除 AuthenticationToken 相关
     * @param accessToken
     */
    public void remove(String accessToken) {
        String refreshToken = (String)redisTemplate.opsForValue().get(AUTH_ACCESS + accessToken);
        Set<String> set = new HashSet<>();
        set.add(AUTH_ACCESS + accessToken);
        set.add(AUTH_ACCESS_TOKEN + accessToken);
        set.add(AUTH_REFRESH_TOKEN + refreshToken);
        redisTemplate.delete(set);
    }

    /**
     * 更新AccessToken中的Principal
     * @param accessToken
     * @param principal
     */
    public void updatePrincipal(String accessToken,CustomUserDetails principal) {
        AuthenticationToken accessAuthenticationToken = readAccessToken(accessToken);
        long accessTokenExpire = accessTokenExpire(accessToken);
        accessAuthenticationToken.setPrincipal(principal);
        String refreshToken = (String)redisTemplate.opsForValue().get(AUTH_ACCESS + accessToken);
        long refreshTokenExpire = readRefreshTokenExpire(refreshToken);
        Map<String, Object> map = new HashMap<>(2);
        map.put(AUTH_ACCESS_TOKEN + accessToken, accessAuthenticationToken);
        map.put(AUTH_REFRESH_TOKEN + refreshToken, accessAuthenticationToken);
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.expire(AUTH_ACCESS_TOKEN + accessToken, accessTokenExpire + 3 , TimeUnit.SECONDS);
        redisTemplate.expire(AUTH_REFRESH_TOKEN + refreshToken, refreshTokenExpire + 3, TimeUnit.SECONDS);
        redisTemplate.expire(AUTH_ACCESS + accessToken,refreshTokenExpire + 3,TimeUnit.SECONDS);
    }


    /**
     * 生成 accessToken
     *
     * @return String
     */
    private String createAccessToken() {
        return UUID.randomUUID().toString();
    }


    /**
     * 生成 accessToken
     *
     * @return String
     */
    private String createRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
