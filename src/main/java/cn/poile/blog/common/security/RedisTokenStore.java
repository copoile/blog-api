package cn.poile.blog.common.security;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.entity.Client;
import cn.poile.blog.service.IClientService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.CustomUserDetails;
import cn.poile.blog.vo.UserVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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
     * key为refreshToken ， value为accessToken，用于refreshToken获取accessToken
     */
    private static final String AUTH_REFRESH_TO_ACCESS = "auth:refresh_to_access:";

    /**
     * key为accessToken ， value为refreshToken，用于refreshToken获取accessToken
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

    @Autowired
    private IUserService userService;


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
     * 存 accessToken
     *
     * @param authentication
     * @return void
     */
    public AuthenticationToken storeToken(Authentication authentication, Client client) {
        // 同一客户端，同一用户是否已登录
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        String clientId = client.getClientId();
        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + extractKey(userId, clientId));
        String access = readAccessByUnameKey(uname2accessKey);
        long accessExpire = client.getAccessTokenExpire() == null ? ACCESS_EXPIRE : client.getAccessTokenExpire();
        long refreshExpire = client.getRefreshTokenExpire() == null ? REFRESH_EXPIRE : client.getRefreshTokenExpire();
        if (StringUtils.isNotBlank(access)) {
            return resetAccessExpire(client.getEnableRefreshToken().equals(1), uname2accessKey, access, accessExpire, userDetails);
        }
        if (client.getEnableRefreshToken().equals(1)) {
            return storeTokenOfAll(uname2accessKey, userId, accessExpire, refreshExpire, userDetails);
        }
        return storeTokenOfOnlyAccess(uname2accessKey, userId, accessExpire, userDetails);

    }

    /**
     * 支持 refresh_token
     *
     * @param uname2accessKey
     * @param userId
     * @param accessExpire
     * @param refreshExpire
     * @param userDetails
     * @return
     */
    private AuthenticationToken storeTokenOfAll(byte[] uname2accessKey, Integer userId, long accessExpire, long refreshExpire, CustomUserDetails userDetails) {
        String access2 = createToken();
        String refresh2 = createToken();
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setTokenType(TOKEN_TYPE);
        authToken.setAccessToken(access2);
        authToken.setRefreshToken(refresh2);
        authToken.setPrincipal(userDetails);

        // redis缓存
        byte[] accessKey = serializedKey(AUTH_ACCESS + access2);
        byte[] refresh2accessKey = serializedKey(AUTH_REFRESH_TO_ACCESS + refresh2);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + access2);
        byte[] refreshKey = serializedKey(AUTH_REFRESH + refresh2);
        // 用户id
        byte[] value = serialized(Integer.toString(userId));
        byte[] accessValue = serialized(access2);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            // access_token : value
            conn.set(accessKey, value, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // userId + clientId : access_token
            conn.set(uname2accessKey, accessValue, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // refresh_token : access_token
            conn.set(refresh2accessKey, accessValue, Expiration.seconds(refreshExpire), RedisStringCommands.SetOption.UPSERT);
            // access_token : refresh_token
            conn.set(access2refreshKey, serialized(refresh2), Expiration.seconds(refreshExpire), RedisStringCommands.SetOption.UPSERT);
            // refresh_token : value
            conn.set(refreshKey, value, Expiration.seconds(refreshExpire), RedisStringCommands.SetOption.UPSERT);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return authToken;
    }


    /**
     * 不支持 refresh_token
     *
     * @param uname2accessKey
     * @param userId
     * @param accessExpire
     * @param userDetails
     * @return
     */
    private AuthenticationToken storeTokenOfOnlyAccess(byte[] uname2accessKey, Integer userId, long accessExpire, CustomUserDetails userDetails) {
        String access = createToken();
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setTokenType(TOKEN_TYPE);
        authToken.setAccessToken(access);
        authToken.setPrincipal(userDetails);

        // redis缓存
        byte[] accessKey = serializedKey(AUTH_ACCESS + access);
        // 用户id
        byte[] value = serialized(Integer.toString(userId));
        byte[] accessValue = serialized(access);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            // access_token : value
            conn.set(accessKey, value, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // userId + clientId : access_token
            conn.set(uname2accessKey, accessValue, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return authToken;
    }


    /**
     * access_token 有效时内重复登录处理
     *
     * @param enableRefreshToken
     * @param uname2accessKey
     * @param access
     * @param accessExpire
     * @param userDetails
     * @return
     */
    private AuthenticationToken resetAccessExpire(boolean enableRefreshToken, byte[] uname2accessKey, String access, long accessExpire, CustomUserDetails userDetails) {
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setAccessToken(access);
        authToken.setTokenType(TOKEN_TYPE);
        authToken.setPrincipal(userDetails);
        if (enableRefreshToken) {
            authToken.setRefreshToken(readRefreshTokenByAccessToken(access));
        }
        // redis缓存
        byte[] accessKey = serializedKey(AUTH_ACCESS + access);
        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            conn.expire(accessKey, accessExpire);
            conn.expire(uname2accessKey, accessExpire);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return authToken;
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
        String idStr = deserializeString(bytes);
        CustomUserDetails customUserDetails = getCustomUserDetails(Integer.parseInt(idStr));
        if (customUserDetails == null) {
            return null;
        }
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setAccessToken(accessToken);
        authToken.setPrincipal(customUserDetails);
        authToken.setRefreshToken(readRefreshTokenByAccessToken(accessToken));
        authToken.setTokenType(TOKEN_TYPE);
        return authToken;
    }

    /**
     * 根据 refreshToken 读取认证信息
     *
     * @param refreshToken
     * @return AuthenticationToken
     */
    public CustomUserDetails getCustomUserDetailsByRefreshToken(String refreshToken) {
        byte[] serializedKey = serializedKey(AUTH_REFRESH + refreshToken);
        RedisConnection conn = getConnection();
        byte[] bytes;
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        if (bytes == null) {
            throw new ApiException(ErrorEnum.REFRESH_CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.REFRESH_CREDENTIALS_INVALID.getErrorMsg());
        }
        String idStr = deserializeString(bytes);
        return getCustomUserDetails(Integer.parseInt(idStr));
    }


    /**
     * 根据 refreshToken 刷新认证信息, access_token 续签
     *
     * @param refreshToken
     * @return
     */
    public AuthenticationToken refreshAuthToken(String refreshToken, Client client) {
        CustomUserDetails userDetails = getCustomUserDetailsByRefreshToken(refreshToken);
        String accessToken = readAccessTokenByRefreshToken(refreshToken);
        if (userDetails == null || StringUtils.isBlank(accessToken)) {
            throw new ApiException(ErrorEnum.REFRESH_CREDENTIALS_INVALID.getErrorCode(), ErrorEnum.REFRESH_CREDENTIALS_INVALID.getErrorMsg());
        }
        AuthenticationToken authToken = new AuthenticationToken();
        authToken.setAccessToken(accessToken);
        authToken.setTokenType(TOKEN_TYPE);
        long accessExpire = client.getAccessTokenExpire() == null ? ACCESS_EXPIRE : client.getAccessTokenExpire();
        authToken.setRefreshToken(refreshToken);
        authToken.setPrincipal(userDetails);
        Integer userId = userDetails.getId();

        byte[] uname2accessKey = serializedKey(UNAME_TO_ACCESS + extractKey(userId, client.getClientId()));
        byte[] accessKey = serializedKey(AUTH_ACCESS + accessToken);
        byte[] value = serialized(Integer.toString(userId));
        byte[] accessValue = serialized(accessToken);

        RedisConnection conn = getConnection();
        try {
            conn.openPipeline();
            // access_token : value
            conn.set(accessKey, value, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            // userId + clientId : access_token
            conn.set(uname2accessKey, accessValue, Expiration.seconds(accessExpire), RedisStringCommands.SetOption.UPSERT);
            conn.closePipeline();
        } finally {
            conn.close();
        }
        return authToken;
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
     * 根据refreshToken 读取 accessToken
     *
     * @param refreshToken
     * @return
     */
    private String readAccessTokenByRefreshToken(String refreshToken) {
        RedisConnection conn = getConnection();
        byte[] serializedKey = serializedKey(AUTH_REFRESH_TO_ACCESS + refreshToken);
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
        byte[] accessKey = serializedKey(AUTH_ACCESS + accessToken);
        byte[] access2refreshKey = serializedKey(AUTH_ACCESS_TO_REFRESH + accessToken);
        byte[] refresh2accessKey = serializedKey(AUTH_REFRESH_TO_ACCESS + refreshToken);
        byte[] refreshKey = serializedKey(AUTH_REFRESH + refreshToken);
        RedisConnection conn = getConnection();
        try {
            conn.del(uname2accessKey, accessKey, access2refreshKey, refreshKey, refresh2accessKey);
        } finally {
            conn.close();
        }
    }


    /**
     * 更新认证信息中的用户信息
     *
     * @param id
     */
    @CacheEvict(value="user",key="#id",beforeInvocation = true)
    public void clearUserCacheById(Integer id) {
        log.info("清除用户{}缓存", id);
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


    /**
     * 获取 CustomUserDetails
     *
     * @param id
     * @return
     */
    private CustomUserDetails getCustomUserDetails(Integer id) {
        UserVo userVo = userService.selectUserVoById(id);
        if (userVo == null) {
            return null;
        }
        CustomUserDetails userDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userVo, userDetails);
        return userDetails;
    }

}
