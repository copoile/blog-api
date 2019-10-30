package cn.poile.blog.common.constant;

/**
 * @author: yaohw
 * @create: 2019-10-30 15:20
 **/
public class RedisConstant {

    /**
     * accessToken key前缀
     */
    public static final String AUTH_ACCESS_TOKEN = "auth:accessToken:";

    /**
     * refreshToken key前缀
     */
    public static final String AUTH_REFRESH_TOKEN = "auth:refreshToken:";

    /**
     * accessToken 时效
     */
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 43200L;

    /**
     * accessToken 时效
     */
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 2592000L;
}
