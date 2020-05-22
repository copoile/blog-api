package cn.poile.blog.common.oauth;

/**
 * @author: yaohw
 * @create: 2020-05-20 11:57
 **/
public class OauthConstant {

    /**
     * QQ 获取accessToken 链接
     */
    public static final String QQ_ACCESS_TOKE_URL = "https://graph.qq.com/oauth2.0/token";

    /**
     * QQ 获取 openid 链接
     */
    public static final String QQ_ACCESS_OPENID_URL = "https://graph.qq.com/oauth2.0/me";

    /**
     * QQ 获取用户信息 链接
     */
    public static final String QQ_ACCESS_USER_URL = "https://graph.qq.com/user/get_user_info";

    /**
     * github 获取accessToken 链接
     */
    public static final String GITHUB_ACCESS_TOKE_URL = "https://github.com/login/oauth/access_token";

    /**
     * github 获取用户信息 链接
     */
    public static final String GITHUB_ACCESS_USER_URL = "https://api.github.com/user";

    /**
     * gitee 获取accessToken 链接
     */
    public static final String GITEE_ACCESS_TOKE_URL = "https://gitee.com/oauth/token";

    /**
     * gitee 获取用户信息 链接
     */
    public static final String GITEE_ACCESS_USER_URL = "https://gitee.com/api/v5/user";

    /**
     * 认证类型QQ
     */
    public static final int OAUTH_TYPE_QQ = 1;

    /**
     * 认证类型github
     */
    public static final int OAUTH_TYPE_GITHUB = 2;

    /**
     * 认证类型gitee
     */
    public static final int OAUTH_TYPE_GITEE = 3;
}
