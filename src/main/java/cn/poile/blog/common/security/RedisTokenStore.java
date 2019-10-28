package cn.poile.blog.common.security;

import cn.poile.blog.vo.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: yaohw
 * @create: 2019-10-28 17:48
 **/
@Log4j2
@Service
public class RedisTokenStore {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String AUTH_TO_ACCESS = "auth:access:";

    private static final String AUTH_ACCESS_TOKEN = "auth:accessToken:";

    private static final String AUTH_REFRESH_TOKEN = "auth:refreshToken:";

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 43200L;

    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 2592000L;

    private static final String USER_ID = "id";

    private static final String USERNAME = "username";

    private String prefix = "";

    /**
     *  存 accessToken
     * @param authentication
     * @return void
     */
    public Token storeAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String extractKey = extractKey(customUserDetails);
        Token token = getAccessToken(extractKey);
        if (token == null || !extractKey.equals(extractKey(token.getPrincipal()))) {
            String accessToken = createAccessToken();
            String refreshToken = createRefreshToken();
            token = new Token();
            token.setAccessToken(accessToken);
            token.setExpire(ACCESS_TOKEN_VALIDITY_SECONDS);
            token.setRefreshToken(refreshToken);
            token.setPrincipal(customUserDetails);
            Map<String, Token> map = new HashMap<>(3);
            map.put(prefix + AUTH_ACCESS_TOKEN + accessToken,token);
            map.put(prefix + AUTH_REFRESH_TOKEN + refreshToken,token);
            map.put(prefix + AUTH_TO_ACCESS + extractKey,token);
            redisTemplate.opsForValue().multiSet(map);
            redisTemplate.expire(prefix + AUTH_ACCESS_TOKEN + accessToken,ACCESS_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
            redisTemplate.expire(prefix + AUTH_TO_ACCESS + extractKey,ACCESS_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
            redisTemplate.expire(prefix + AUTH_REFRESH_TOKEN + refreshToken,REFRESH_TOKEN_VALIDITY_SECONDS,TimeUnit.SECONDS);
        }
        return token;
    }

    /**
     *  读 accessToken
     * @param accessToken
     * @return cn.poile.blog.vo.TokenVo
     */
    public Token readAccessToken(String accessToken) {
        return (Token)redisTemplate.opsForValue().get(prefix + AUTH_ACCESS_TOKEN + accessToken);
    }

    /**
     *  读 refreshToken
     * @param refreshToken
     * @return cn.poile.blog.vo.TokenVo
     */
    public Token readRefreshToken(String refreshToken) {
        return (Token)redisTemplate.opsForValue().get(prefix + AUTH_REFRESH_TOKEN + refreshToken);
    }

    /**
     * 获取 accessToken
     * @param extractKey
     * @return cn.poile.blog.vo.TokenVo
     */
    private Token getAccessToken(String extractKey) {
        Token token = (Token)redisTemplate.opsForValue().get(prefix + AUTH_TO_ACCESS + extractKey);
        log.info("token:{}",token);
        return token;
    }

    /**
     * 生成 accessToken
     * @return String
     */
    private String createAccessToken() {
        return UUID.randomUUID().toString();
    }


    /**
     * 生成 accessToken
     * @return String
     */
    private String createRefreshToken() {
        return UUID.randomUUID().toString();
    }


    /**
     * key 提取，主要用来判断是否同一个用户
     * @param customUserDetails
     * @return String
     */
    private String extractKey(CustomUserDetails customUserDetails) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(USER_ID, String.valueOf(customUserDetails.getId()));
        values.put(USERNAME,customUserDetails.getUsername());
        return generateKey(values);
    }

    /**
     * MD5 加密key
     * @param values
     * @return String
     */
    private String generateKey(Map<String, String> values) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(values.toString().getBytes(StandardCharsets.UTF_8 ));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
        }
    }

}
