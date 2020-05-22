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

import static cn.poile.blog.common.oauth.OauthConstant.GITHUB_ACCESS_TOKE_URL;
import static cn.poile.blog.common.oauth.OauthConstant.GITHUB_ACCESS_USER_URL;

/**
 * github 第三方登录
 * @author: yaohw
 * @create: 2020-05-20 11:49
 **/
@Log4j2
@Service
public class GithubThirdAuth{


    @Value("${oauth.github.clientId}")
    public String clientId;

    @Value("${oauth.github.clientSecret}")
    public String clientSecret;

    /**
     * 获取第三方用户信息
     *
     * @param accessToken
     * @return
     */
    public ThirdAuthUser getUserInfoByToken(String accessToken) {
        Map<String, String> params = new HashMap<>(2);
        params.put("access_token",accessToken);
        String result = HttpClientUtil.doGet(GITHUB_ACCESS_USER_URL, params);
        if (StringUtils.isBlank(result)) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"获取第三方用户信息出错");
        }
        log.info("请求结果:" + result);
        JSONObject jsonObject = JSON.parseObject(result);
        ThirdAuthUser thirdAuthUser = new ThirdAuthUser();
        thirdAuthUser.setUuid(jsonObject.getString("id"));
        thirdAuthUser.setNickname(jsonObject.getString("name"));
        thirdAuthUser.setAvatar(jsonObject.getString("avatar_url"));
        thirdAuthUser.setEmail(jsonObject.getString("email"));
        return thirdAuthUser;
    }


    /**
     * 获取第三方用户信息
     *
     * @param code
     * @return
     */
    public ThirdAuthUser getUserInfoByCode(String code) {
        ThirdAuthToken thirdAuthToken = getAuthToken(code);
        return getUserInfoByToken(thirdAuthToken.getAccessToken());
    }

    /**
     * 获取第三方token信息
     *
     * @param code
     * @return
     */
    public ThirdAuthToken getAuthToken(String code) {
        Map<String, String> params = new HashMap<>(4);
        params.put("client_id",clientId);
        params.put("client_secret",clientSecret);
        params.put("code",code);
        String result = HttpClientUtil.doGet(GITHUB_ACCESS_TOKE_URL, params);
        if (StringUtils.isBlank(result)) {
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"获取第三方token出错");
        }
        ThirdAuthToken thirdAuthToken = new ThirdAuthToken();
        thirdAuthToken.setAccessToken(getAccessToken(result));
        return thirdAuthToken;
    }

    /**
     * 获取 access_token
     * @param result
     * @return
     */
    private String getAccessToken(String result) {
        // result
        // access_token=aa5a59cd212b2c0f3c1f285822b2085f52fe3850&scope=user%3Aemail&token_type=bearer
        try {
            return result.split("&")[0].split("=")[1];
        }catch (ArrayIndexOutOfBoundsException e) {
            log.error("获取access_token异常", e);
            throw new ApiException(ErrorEnum.SYSTEM_ERROR.getErrorCode(),"获取第三方token出错");
        }
    }
}
