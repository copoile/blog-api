package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.vo.CustomUserDetails;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * accessToken 生成、存储
 *
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
     * user md5 : accessToken
     */
    private static final String AUTH_USER_ACCESS = "auth:user_to_access:";

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

    /**
     * 获取 redis 连接
     *
     * @return
     */
    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    /**
     * 对象value序列化
     *
     * @param object
     * @return
     */
    private byte[] serializedAuthentication(Object object) {
        return jdkSerializationRedisSerializer.serialize(object);
    }

    /**
     * string value 序列化
     *
     * @param v
     * @return
     */
    private byte[] serialized(String v) {
        return stringRedisSerializer.serialize(v);
    }

    /**
     * key序列化
     *
     * @param k
     * @return
     */
    private byte[] serializedKey(String k) {
        return stringRedisSerializer.serialize(k);
    }

    /**
     * string 反序列化
     *
     * @param bytes
     * @return
     */
    private String deserializeString(byte[] bytes) {
        return stringRedisSerializer.deserialize(bytes);
    }


    /**
     * 反序列化
     *
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
        byte[] serializedAuthentication = serializedAuthentication(token);
        byte[] accessTokenKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
        byte[] refreshTokenKey = serializedKey(AUTH_REFRESH_TOKEN + refreshToken);
        byte[] accessKey = serializedKey((AUTH_ACCESS + accessToken));
        byte[] serializeRefreshToken = serialized(refreshToken);
        String extract = extractKey(customUserDetails.getId());
        byte[] extractKey = serializedKey(AUTH_USER_ACCESS + extract);
        byte[] serializedAccessToken = serialized(accessToken);
        long now = Instant.now().toEpochMilli();
        long expired = now - TimeUnit.SECONDS.toMillis(ACCESS_TOKEN_VALIDITY_SECONDS);
        try {
            conn.openPipeline();
            conn.set(accessTokenKey, serializedAuthentication, Expiration.seconds(ACCESS_TOKEN_VALIDITY_SECONDS), RedisStringCommands.SetOption.UPSERT);
            conn.set(refreshTokenKey, serializedAuthentication, Expiration.seconds(REFRESH_TOKEN_VALIDITY_SECONDS), RedisStringCommands.SetOption.UPSERT);
            conn.set(accessKey, serializeRefreshToken, Expiration.seconds(REFRESH_TOKEN_VALIDITY_SECONDS), RedisStringCommands.SetOption.UPSERT);
            conn.zSetCommands().zRemRangeByScore(extractKey, 0, expired);
            conn.zSetCommands().zAdd(extractKey, now, serializedAccessToken);
            conn.expire(extractKey, REFRESH_TOKEN_VALIDITY_SECONDS);
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
        byte[] serializedKey = serializedKey(AUTH_REFRESH_TOKEN + refreshToken);
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
     * 刷新 accessToken
     *
     * @param refreshToken
     * @return
     */
    public AuthenticationToken refreshAccessToken(String refreshToken) {
        AuthenticationToken authenticationToken = readRefreshToken(refreshToken);
        if (authenticationToken == null) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        String accessToken = authenticationToken.getAccessToken();
        byte[] serializedAuthentication = serializedAuthentication(authenticationToken);
        byte[] serializedKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
        String extract = extractKey(authenticationToken.getPrincipal().getId());
        byte[] extractKey = serializedKey(AUTH_USER_ACCESS + extract);
        byte[] serializedAccessToken = serialized(accessToken);
        long now = Instant.now().toEpochMilli();
        long expired = now - TimeUnit.SECONDS.toMillis(ACCESS_TOKEN_VALIDITY_SECONDS);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.set(serializedKey, serializedAuthentication, Expiration.seconds(ACCESS_TOKEN_VALIDITY_SECONDS), RedisStringCommands.SetOption.UPSERT);
            conn.zSetCommands().zRemRangeByScore(extractKey, 0, expired);
            conn.zSetCommands().zAdd(extractKey, now, serializedAccessToken);
            conn.expire(extractKey, REFRESH_TOKEN_VALIDITY_SECONDS);
        } finally {
            conn.close();

        }
        return authenticationToken;
    }

    /**
     * accessToken 过期时间
     *
     * @param accessToken
     * @return
     */
    private Long accessTokenExpire(String accessToken) {
        byte[] serializedKey = serializedKey(accessToken);
        RedisConnection conn = getConnection();
        Long ttl;
        try {
            ttl = conn.ttl(serializedKey, TimeUnit.SECONDS);
        } finally {
            conn.close();
        }
        return ttl == null ? -2 : ttl;
    }

    /**
     * refreshToken 过期时间
     *
     * @param refreshToken
     * @return
     */
    private long readRefreshTokenExpire(String refreshToken) {
        byte[] serializedKey = serializedKey(refreshToken);
        RedisConnection conn = getConnection();
        Long ttl;
        try {
            ttl = conn.ttl(serializedKey, TimeUnit.SECONDS);
        } finally {
            conn.close();
        }
        return ttl == null ? -2 : ttl;
    }

    /**
     * 根据accessToken 读取 refreshToken
     *
     * @param accessToken
     * @return
     */
    private String readAccessToRefreshToken(String accessToken) {
        RedisConnection conn = getConnection();
        byte[] serializedKey = serializedKey(AUTH_ACCESS + accessToken);
        byte[] bytes;
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        return deserializeString(bytes);
    }

    /**
     * 移除 AuthenticationToken 相关
     *
     * @param accessToken
     */
    public void remove(String accessToken) {
        String refreshToken = readAccessToRefreshToken(accessToken);
        if (StringUtils.isBlank(refreshToken)) {
            return;
        }
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail();
        assert userDetail != null;
        String extract = extractKey(userDetail.getId());
        byte[] extractKey = serializedKey(extract);
        byte[] accessTokenKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
        byte[] refreshTokenKey = serializedKey(AUTH_REFRESH_TOKEN + refreshToken);
        byte[] accessKey = serializedKey((AUTH_ACCESS + accessToken));
        byte[] serializedAccessToken = serialized(accessToken);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.del(accessTokenKey, refreshTokenKey, accessKey);
            conn.zSetCommands().zRem(extractKey, serializedAccessToken);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    /**
     * 获取用户登录所有 accessToken
     *
     * @param userId
     * @return
     */
    private Set<String> extractAccessToken(long userId) {
        String extract = extractKey(userId);
        byte[] serializedKey = serializedKey(extract);
        RedisConnection conn = getConnection();
        Set<byte[]> set;
        try {
            set = conn.zSetCommands().zRange(serializedKey, 0, -1);
        } finally {
            conn.close();
        }
        return set == null ? new HashSet<>(0) : set.stream().map(this::deserializeString).collect(Collectors.toSet());
    }

    /**
     * 更新AccessToken中的Principal
     *
     * @param principal
     */
    public void updatePrincipal(CustomUserDetails principal) {
        Set<String> accessTokenSet = extractAccessToken(principal.getId());
        RedisConnection conn = getConnection();
        try {
            accessTokenSet.forEach(
                    accessToken -> {
                        AuthenticationToken accessAuthenticationToken = readAccessToken(accessToken);
                        long accessTokenExpire = accessTokenExpire(accessToken);
                        accessAuthenticationToken.setPrincipal(principal);
                        byte[] serializedAuthentication = serializedAuthentication(accessAuthenticationToken);
                        String refreshToken = readAccessToRefreshToken(accessToken);
                        byte[] serializeRefreshToken = serialized(refreshToken);
                        long refreshTokenExpire = readRefreshTokenExpire(refreshToken);
                        byte[] accessTokenKey = serializedKey(AUTH_ACCESS_TOKEN + accessToken);
                        byte[] refreshTokenKey = serializedKey(AUTH_REFRESH_TOKEN + refreshToken);
                        byte[] accessKey = serializedKey((AUTH_ACCESS + accessToken));
                        conn.openPipeline();
                        conn.set(accessTokenKey, serializedAuthentication, Expiration.seconds(accessTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.set(refreshTokenKey, serializedAuthentication, Expiration.seconds(refreshTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.set(accessKey, serializeRefreshToken, Expiration.seconds(refreshTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.closePipeline();
                    }
            );
        } finally {
            conn.close();
        }
    }

    /**
     * key 提取，主要用来判断是否同一个用户
     *
     * @param userId
     * @return String
     */
    private String extractKey(long userId) {
        String k = "id";
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(k, String.valueOf(userId));
        return generateKey(values);
    }

    /**
     * MD5 加密key
     *
     * @param values
     * @return String
     */
    private String generateKey(Map<String, String> values) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(values.toString().getBytes(StandardCharsets.UTF_8));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
        }
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
