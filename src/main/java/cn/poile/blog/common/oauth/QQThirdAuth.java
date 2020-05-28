package cn.poile.blog.common.oauth;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.util.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * QQ 第三方登录
 *
 * @author: yaohw
 * @create: 2020-05-21 18:36
 **/
@Service
@Log4j2
public class QQThirdAuth {

    @Value("${oauth.qq.appId}")
    public String appId;

    @Value("${oauth.qq.clientId}")
    public String clientId;

    @Value("${oauth.qq.clientSecret}")
    public String clientSecret;

    @Value("${oauth.qq.redirect_uri}")
    public String redirect;

    /**
     * 获取第三方用户信息
     *
     * @param accessToken
     * @return
     */
    public ThirdAuthUser getUserInfo(String accessToken, String openid) {
        Map<String, String> params = new HashMap<>(4);
        params.put("access_token", accessToken);
        params.put("openid", openid);
        params.put("oauth_consumer_key", appId);
        String result = HttpClientUtil.doGet(OauthConstant.QQ_ACCESS_USER_URL, params);
        if (StringUtils.isBlank(result)) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "获取第三方用户信息出错");
        }
        JSONObject jsonObject = JSON.parseObject(result);
        ThirdAuthUser thirdAuthUser = new ThirdAuthUser();
        thirdAuthUser.setUuid(openid);
        thirdAuthUser.setNickname(jsonObject.getString("nickname"));
        // 头像取100*100的，但100*100的不一定都有
        String avatar = StringUtils.isBlank(jsonObject.getString("figureurl_qq_2")) ? jsonObject.getString("figureurl_qq_1") : jsonObject.getString("figureurl_qq_2");
        thirdAuthUser.setAvatar(avatar);
        return thirdAuthUser;
    }

    /**
     * 获取第三方token信息
     *
     * @param code
     * @return
     */
    public ThirdAuthToken getAuthToken(String code) {
        Map<String, String> params = new HashMap<>(8);
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirect);
        String result = HttpClientUtil.doGet(OauthConstant.QQ_ACCESS_TOKE_URL, params);
        if (StringUtils.isBlank(result)) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "获取第三方token出错");
        }
        ThirdAuthToken thirdAuthToken = new ThirdAuthToken();
        thirdAuthToken.setAccessToken(getAccessToken(result));
        return thirdAuthToken;
    }

    /**
     * 获取openid
     *
     * @param accessToken
     * @return
     */
    public String getOpenid(String accessToken) {
        Map<String, String> params = new HashMap<>(2);
        params.put("access_token", accessToken);
        String result = HttpClientUtil.doGet(OauthConstant.QQ_ACCESS_OPENID_URL, params);
        if (StringUtils.isBlank(result)) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "获取openid出错");
        }
        // callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
        String json = result.replaceAll("(callback)|(\\()|(\\))|(\\))|(;)", "");
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }

    /**
     * 获取 access_token
     *
     * @param result
     * @return
     */
    private String getAccessToken(String result) {
        // result
        // access_token=aa5a59cd212b2c0f3c1f285822b2085f52fe3850&scope=user%3Aemail&token_type=bearer
        try {
            return result.split("&")[0].split("=")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("获取access_token异常", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(), "获取第三方token出错");
        }
    }
}
