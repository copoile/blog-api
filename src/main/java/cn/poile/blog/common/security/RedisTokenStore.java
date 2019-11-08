package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.vo.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
    private static final String AUTH_ACCESS_TOKEN = "auth:access_token:";

    /**
     * refreshToken key前缀
     */
    private static final String AUTH_REFRESH_TOKEN = "auth:refresh_token:";

    /**
     * accessToken : refreshToken
     */
    private static final String AUTH_ACCESS = "auth:access_to_refresh:";

    /**
     * accessToken 时效
     */
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 43200L;

    /**
     * accessToken 时效
     */
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 2592000L;

    @Autowired
    private RedisConnectionFactory connectionFactory;


    private JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

    private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    /**
     * value序列化
     * @param object
     * @return
     */
    private byte[] serializedAuthentication(Object object) {
        return jdkSerializationRedisSerializer.serialize(object);
    }

    /**
     * key序列化
     * @param string
     * @return
     */
    private byte[] serializedKey(String string) {
        return stringRedisSerializer.serialize(string);
    }



    /**
     * 反序列化
     * @param bytes
     * @return
     */
    private AuthenticationToken deserializeAuthentication(byte[] bytes) {
        return (AuthenticationToken) jdkSerializationRedisSerializer.deserialize(bytes);
    }


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
        RedisConnection conn = getConnection();
        byte[] serializedAuthentication = serializedAuthentication(authentication);
        byte[] accessTokenKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
        byte[] refreshTokenKey = serializedKey(AUTH_REFRESH_TOKEN + refreshToken);
        byte[] accessKey = serializedKey((AUTH_ACCESS + accessToken));
        byte[] access = serializedKey(accessToken);
        try {
            conn.openPipeline();
            conn.set(accessTokenKey, serializedAuthentication);
            conn.set(refreshTokenKey,serializedAuthentication);
            conn.set(accessKey,access);
            conn.expire(accessTokenKey,ACCESS_TOKEN_VALIDITY_SECONDS);
            conn.expire(refreshTokenKey,REFRESH_TOKEN_VALIDITY_SECONDS);
            conn.expire(accessKey,REFRESH_TOKEN_VALIDITY_SECONDS);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return token;
    }

    /**
     * 读 accessToken
     *
     * @param accessToken
     * @return AuthenticationToken
     */
    public AuthenticationToken readAccessToken(String accessToken) {
        byte[] serializedKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        return deserializeAuthentication(bytes);
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
