package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.entity.Client;
import cn.poile.blog.service.IClientService;
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
     * token类型
     */
    private static final String TOKEN_TYPE = "Bearer";

    /**
     * accessToken key前缀
     */
    private static final String AUTH_ACCESS = "auth:access:";

    /**
     * refreshToken key前缀
     */
    private static final String AUTH_REFRESH = "auth:refresh:";

    /**
     * key为accessToken ， value为refreshToken，用于accessToken获取refreshToken
     */
    private static final String AUTH_ACCESS_TO_REFRESH = "auth:access_to_refresh:";

    /**
     * userId + clientId
     */
    private static final String UNAME_TO_ACCESS = "auth:uname_to_access:";

    /**
     * user md5 : accessToken，这是zset的key，列表里放此用户登录的accessToken
     */
    private static final String AUTH_USER_ACCESS = "auth:user_to_access:";

    /**
     * 默认accessToken 时效,两小时
     */
    private static final long ACCESS_EXPIRE = 7200;

    /**
     * 默认accessToken 时效，30天
     */
    private static final long REFRESH_EXPIRE = 2592000;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private IClientService clientService;


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
    public AuthenticationToken storeAccessToken(Authentication authentication, Client client) {
        // 同一客户端，同一用户是否已登录
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        String clientId = client.getClientId();
        String uname = extractKey(userId, clientId);
        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + uname);
        byte[] user2accessKey = serializedKey(AUTH_USER_ACCESS + extractKey(userId, null));
        String access = readAccessByUnameKey(uname2accessKey);
        long accessExpire = client.getAccessTokenExpire() == null ? ACCESS_EXPIRE : client.getAccessTokenExpire();
        long refreshExpire = client.getRefreshTokenExpire() == null ? REFRESH_EXPIRE : client.getRefreshTokenExpire();
        // 已登录, 重置时效
        if (!StringUtils.isBlank(access)) {
            AuthenticationToken authToken = new AuthenticationToken();
            authToken.setAccessToken(access);
            authToken.setExpire(accessExpire);
            String refresh = readRefreshByAccess(access);
            authToken.setRefreshToken(refresh);
            authToken.setPrincipal(userDetails);
            authToken.setClientId(clientId);
            authToken.setTokenType(TOKEN_TYPE);
            byte[] serializedAuthentication = serializedAuthentication(authToken);
            restAccessExpireAndRefreshExpire(user2accessKey, uname2accessKey, access, accessExpire, refresh, refreshExpire, serializedAuthentication);
            return authToken;
        }
        // 未登录
        String access2 = createToken();
        String refresh2 = createToken();
        // 构建对象
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setAccessToken(access2);
        authToken.setExpire(accessExpire);
        authToken.setRefreshToken(refresh2);
        authToken.setPrincipal(userDetails);
        authToken.setClientId(clientId);
        authToken.setTokenType(TOKEN_TYPE);
        // redis缓存
        byte[] accessKey = serializedKey(AUTH_ACCESS + access2);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + access2);
        byte[] refreshKey = serializedKey(AUTH_REFRESH + refresh2);
        byte[] serializedAuthentication = serializedAuthentication(authToken);
        long now = Instant.now().toEpochMilli();
        long expired = now - TimeUnit.SECONDS.toMillis(accessExpire);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            // accessToken : Authentication
            conn.set(accessKey, serializedAuthentication, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // userId + clientId : accessToken
            conn.set(uname2accessKey, serialized(access2), Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // accessToken : refreshToken
            conn.set(access2refreshKey, serialized(refresh2), Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // refreshToken : Authentication
            conn.set(refreshKey, serializedAuthentication, Expiration.seconds(refreshExpire), RedisStringCommands.SetOption.UPSERT);
            // 移除 zset 中过期数据
            conn.zSetCommands().zRemRangeByScore(user2accessKey, 0, expired);
            // userId: accessToken
            conn.zSetCommands().zAdd(user2accessKey, now, serialized(access2));
            conn.expire(user2accessKey, refreshExpire);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return authToken;
    }

    /**
     * 同一客户端，同一用户重复登录重置时效
     *
     * @param user2accessKey
     * @param uname2accessKey
     * @param access
     * @param accessExpire
     * @param refresh
     * @param refreshExpire
     */
    private void restAccessExpireAndRefreshExpire(byte[] user2accessKey, byte[] uname2accessKey, String access, long accessExpire, String refresh, long refreshExpire, byte[] serializedAuthentication) {
        byte[] accessKey = serializedKey(AUTH_ACCESS + access);
        byte[] refreshKey = serializedKey(AUTH_REFRESH + refresh);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + access);
        long now = Instant.now().toEpochMilli();
        long expired = now - TimeUnit.SECONDS.toMillis(accessExpire);
        byte[] accessSerialized = serialized(access);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.expire(accessKey, accessExpire);
            conn.expire(uname2accessKey, accessExpire);
            conn.expire(refreshKey, refreshExpire);
            conn.expire(access2refreshKey, refreshExpire);
            conn.zSetCommands().zRemRangeByScore(user2accessKey, 0, expired);
            conn.zSetCommands().zRem(user2accessKey, accessSerialized);
            conn.zSetCommands().zAdd(user2accessKey, now,accessSerialized );
            conn.expire(user2accessKey, refreshExpire);
            conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    /**
     * 根据 unameKey 读取accessToken
     *
     * @param unameKey
     * @return
     */
    private String readAccessByUnameKey(byte[] unameKey) {
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(unameKey);
        } finally {
            conn.close();
        }
        return deserializeString(bytes);
    }

    /**
     * 根据 accessToken 读取refreshToken
     *
     * @param accessToken
     * @return
     */
    private String readRefreshByAccess(String accessToken) {
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + accessToken);
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(access2refreshKey);
        } finally {
            conn.close();
        }
        return deserializeString(bytes);
    }

    /**
     * 根据 accessToken 读取认证信息
     *
     * @param accessToken
     * @return AuthenticationToken
     */
    public AuthenticationToken readByAccessToken(String accessToken) {
        byte[] serializedKey = serializedKey(AUTH_ACCESS + accessToken);
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
     * 根据 refreshToken 读取认证信息
     *
     * @param refreshToken
     * @return AuthenticationToken
     */
    public AuthenticationToken readByRefreshToken(String refreshToken) {
        byte[] serializedKey = serializedKey(AUTH_REFRESH + refreshToken);
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
     * 根据 refreshToken 刷新认证信息
     *
     * @param refreshToken
     * @return
     */
    public AuthenticationToken refreshAuthToken(String refreshToken, Client client) {
        AuthenticationToken authToken = readByRefreshToken(refreshToken);
        if (authToken == null) {
            throw new ApiException(ErrorEnum.CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.CREDENTIALS_INVALID.getErrorMsg());
        }
        CustomUserDetails userDetails = authToken.getPrincipal();
        Integer userId = userDetails.getId();
        String clientId = client.getClientId();
        String uname = extractKey(userId, clientId);
        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + uname);
        byte[] user2accessKey = serializedKey(AUTH_USER_ACCESS + extractKey(userId, null));
        long accessExpire = client.getAccessTokenExpire() == null ? ACCESS_EXPIRE : client.getAccessTokenExpire();
        long refreshExpire = client.getRefreshTokenExpire() == null ? REFRESH_EXPIRE : client.getRefreshTokenExpire();
        long now = Instant.now().toEpochMilli();
        long expired = now - TimeUnit.SECONDS.toMillis(accessExpire);
        String access = authToken.getAccessToken();
        String refresh = authToken.getRefreshToken();
        byte[] accessKey = serializedKey(AUTH_ACCESS + access);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + access);
        byte[] serializedAuthentication = serializedAuthentication(authToken);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.set(accessKey, serializedAuthentication, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            conn.set(uname2accessKey, serialized(access), Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            conn.set(access2refreshKey, serialized(refresh), Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            conn.zSetCommands().zRemRangeByScore(user2accessKey, 0, expired);
            conn.zSetCommands().zAdd(user2accessKey, now, serialized(access));
            conn.expire(user2accessKey, refreshExpire);
        } finally {
            conn.close();
        }
        return authToken;
    }

    /***
     * 刷新 accessToken 时间
     * @param authToken
     * @return
     */
    public void refreshAccessExpire(AuthenticationToken authToken) {
        String clientId = authToken.getClientId();
        Client client = clientService.getClientByClientId(clientId);
        long accessExpire = client.getAccessTokenExpire() == null ? ACCESS_EXPIRE : client.getAccessTokenExpire();
        long refreshExpire = client.getRefreshTokenExpire() == null ? REFRESH_EXPIRE : client.getRefreshTokenExpire();
        CustomUserDetails userDetails = authToken.getPrincipal();
        Integer userId = userDetails.getId();
        String uname = extractKey(userId, clientId);
        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + uname);
        byte[] user2accessKey = serializedKey(AUTH_USER_ACCESS + extractKey(userId, null));
        String access = authToken.getAccessToken();
        byte[] accessKey = serializedKey(AUTH_ACCESS + access);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + access);
        long now = Instant.now().toEpochMilli();
        byte[] accessSerialized = serialized(access);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.expire(accessKey, accessExpire);
            conn.expire(uname2accessKey, accessExpire);
            conn.expire(access2refreshKey, refreshExpire);
            conn.zSetCommands().zRem(user2accessKey, accessSerialized);
            conn.zSetCommands().zAdd(user2accessKey, now, accessSerialized);
            conn.expire(user2accessKey, refreshExpire);
            conn.closePipeline();
        } finally {
            conn.close();

        }
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
    private String readRefreshTokenByAccessToken(String accessToken) {
        RedisConnection conn = getConnection();
        byte[] serializedKey = serializedKey(AUTH_ACCESS_TO_REFRESH + accessToken);
        byte[] bytes;
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        return deserializeString(bytes);
    }

    /**
     * 移除 AuthenticationToken 相关,退出调用
     *
     * @param accessToken
     * @param client
     */
    public void remove(String accessToken, Client client) {
        String refreshToken = readRefreshTokenByAccessToken(accessToken);
        if (StringUtils.isBlank(refreshToken)) {
            return;
        }
        AuthenticationToken authToken = readByAccessToken(accessToken);
        if (authToken == null) {
            return;
        }
        CustomUserDetails userDetail = authToken.getPrincipal();
        Integer userId = userDetail.getId();
        String clientId = client.getClientId();
        String uname = extractKey(userId, clientId);
        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + uname);
        byte[] user2accessKey = serializedKey(AUTH_USER_ACCESS + extractKey(userId, null));
        byte[] accessKey = serializedKey(AUTH_ACCESS + accessToken);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + accessToken);
        byte[] refreshKey = serializedKey(AUTH_REFRESH + refreshToken);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            // 多key删除
            conn.del(uname2accessKey, accessKey, access2refreshKey, refreshKey);
            // zset指定删除
            conn.zSetCommands().zRem(user2accessKey, serialized(accessToken));
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
    private Set<String> extractAccessToken(Integer userId) {
        String extract = extractKey(userId, null);
        byte[] extractKey = serializedKey(AUTH_USER_ACCESS + extract);
        RedisConnection conn = getConnection();
        Set<byte[]> set;
        try {
            set = conn.zSetCommands().zRange(extractKey, 0, -1);
        } finally {
            conn.close();
        }
        return set == null ? new HashSet<>(0) : set.stream().map(this::deserializeString).collect(Collectors.toSet());
    }

    /**
     * 更新认证信息中的用户信息
     *
     * @param principal
     */
    public void updatePrincipal(CustomUserDetails principal) {
        Integer userId = principal.getId();
        Set<String> accessTokenSet = extractAccessToken(userId);
        RedisConnection conn = getConnection();
        try {
            accessTokenSet.forEach(
                    accessToken -> {
                        AuthenticationToken accessAuthenticationToken = readByAccessToken(accessToken);
                        long accessTokenExpire = accessTokenExpire(AUTH_ACCESS + accessToken);
                        accessAuthenticationToken.setPrincipal(principal);
                        byte[] serializedAuthentication = serializedAuthentication(accessAuthenticationToken);
                        String refreshToken = readRefreshTokenByAccessToken(accessToken);
                        byte[] serializeRefreshToken = serialized(refreshToken);
                        long refreshTokenExpire = readRefreshTokenExpire(AUTH_REFRESH + refreshToken);
                        byte[] accessKey = serializedKey(AUTH_ACCESS + accessToken);
                        byte[] refreshKey = serializedKey(AUTH_REFRESH + refreshToken);
                        byte[] access2refreshKey = serializedKey((AUTH_ACCESS_TO_REFRESH + accessToken));
                        conn.openPipeline();
                        conn.set(accessKey, serializedAuthentication, Expiration.seconds(accessTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.set(refreshKey, serializedAuthentication, Expiration.seconds(refreshTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.set(access2refreshKey, serializeRefreshToken, Expiration.seconds(refreshTokenExpire + 3), RedisStringCommands.SetOption.UPSERT);
                        conn.closePipeline();
                    }
            );
        } finally {
            conn.close();
        }
    }

    /**
     * key 提取，主要用来判断是否同一客户端同一用户登录
     *
     * @param userId
     * @param clientId
     * @return String
     */
    private String extractKey(long userId, String clientId) {
        String userIdKey = "userId";
        String clientIdKey = "clientId";
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(userIdKey, String.valueOf(userId));
        if (!StringUtils.isBlank(clientId)) {
            values.put(clientIdKey, clientId);
        }
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
     * 生成 Token
     *
     * @return String
     */
    private String createToken() {
        return UUID.randomUUID().toString();
    }

}
