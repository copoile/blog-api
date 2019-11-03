package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.vo.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    public AccessToken storeAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = createAccessToken();
        String refreshToken = createRefreshToken();
        AccessToken token = new AccessToken();
        token.setAccessToken(accessToken);
        token.setExpire(ACCESS_TOKEN_VALIDITY_SECONDS);
        token.setRefreshToken(refreshToken);
        token.setUser(customUserDetails);
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
     * @return AccessToken
     */
    public AccessToken readAccessToken(String accessToken) {
        return (AccessToken) redisTemplate.opsForValue().get(AUTH_ACCESS_TOKEN + accessToken);
    }

    /**
     * 读 refreshToken
     *
     * @param refreshToken
     * @return AccessToken
     */
    public AccessToken readRefreshToken(String refreshToken) {
        return (AccessToken) redisTemplate.opsForValue().get(AUTH_REFRESH_TOKEN + refreshToken);
    }

    /**
     * 刷新 accessToken
     * @param refreshToken
     * @return
     */
    public AccessToken refreshAccessToken(String refreshToken) {
        AccessToken accessToken = readRefreshToken(refreshToken);
        if (accessToken == null) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(),ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        String key = accessToken.getAccessToken();
        redisTemplate.opsForValue().set(AUTH_ACCESS_TOKEN + key,accessToken,ACCESS_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 移除 AccessToken 相关
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
