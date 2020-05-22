package cn.poile.blog.biz;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.poile.blog.common.oauth.*;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.constant.UserConstant;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.PrimaryKeyAuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.entity.Client;
import cn.poile.blog.entity.OauthUser;
import cn.poile.blog.entity.User;
import cn.poile.blog.service.IOauthUserService;
import cn.poile.blog.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 第三方认证
 * @author: yaohw
 * @create: 2020-05-20 15:33
 **/
@Service
@Log4j2
public class OauthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTokenStore tokenStore;

    @Autowired
    private GithubThirdAuth githubThirdAuth;

    @Autowired
    private GiteeThirdAuth giteeThirdAuth;

    @Autowired
    private IOauthUserService oauthUserService;

    @Autowired
    private QQThirdAuth qqThirdAuth;

    @Autowired
    private IUserService userService;

    /**
     * 第三方登录
     * @param type
     * @param code
     * @param client
     * @return
     */
    public AuthenticationToken oauth(int type, String code, Client client) {
        if (type == OauthConstant.OAUTH_TYPE_GITHUB) {
            return githubOauth(type,code,client);
        } else if (type == OauthConstant.OAUTH_TYPE_GITEE) {
            return giteeOauth(type, code, client);
        } else if (type == OauthConstant.OAUTH_TYPE_QQ) {
            return qqOauth(type,code,client);
        }
        throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(),"无效类型type");
    }

    /**
     * github第三方登录
     * @param type
     * @param code
     * @param client
     * @return
     */
    private AuthenticationToken githubOauth(int type, String code, Client client) {
        ThirdAuthUser thirdAuthUser = githubThirdAuth.getUserInfoByCode(code);
        String uuid = thirdAuthUser.getUuid();
        OauthUser oauthUser = checkBind(type, uuid);
        // 已绑定
        if (oauthUser != null) {
            PrimaryKeyAuthenticationToken authenticationToken = new PrimaryKeyAuthenticationToken(oauthUser.getUserId());
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            return tokenStore.storeToken(authenticate,client);
        }
        // 未绑定
        return bind(type,thirdAuthUser,client);
    }

    /**
     * gitee 第三方登录
     * @param type
     * @param code
     * @param client
     * @return
     */
    private AuthenticationToken giteeOauth(int type, String code, Client client) {
        ThirdAuthUser thirdAuthUser = giteeThirdAuth.getUserInfoByCode(code);
        String uuid = thirdAuthUser.getUuid();
        OauthUser oauthUser = checkBind(type, uuid);
        // 已绑定
        if (oauthUser != null) {
            PrimaryKeyAuthenticationToken authenticationToken = new PrimaryKeyAuthenticationToken(oauthUser.getUserId());
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            return tokenStore.storeToken(authenticate,client);
        }
        // 未绑定
        return bind(type,thirdAuthUser,client);
    }

    /**
     * qq 第三方登录
     * @param type
     * @param code
     * @param client
     * @return
     */
    private AuthenticationToken qqOauth(int type, String code, Client client) {
        ThirdAuthToken authToken = qqThirdAuth.getAuthToken(code);
       String openid = qqThirdAuth.getOpenid(authToken.getAccessToken());
        OauthUser oauthUser = checkBind(type, openid);
        if (oauthUser != null) {
            PrimaryKeyAuthenticationToken authenticationToken = new PrimaryKeyAuthenticationToken(oauthUser.getUserId());
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            return tokenStore.storeToken(authenticate,client);
        }
        ThirdAuthUser thirdAuthUser = qqThirdAuth.getUserInfo(authToken.getAccessToken(), openid);
        return bind(type,thirdAuthUser,client);
    }


    /**
     * 查询是否已绑定
     * @param type
     * @param uuid
     * @return
     */
    private OauthUser checkBind(int type, String uuid) {
        QueryWrapper<OauthUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OauthUser::getUuid,uuid).eq(OauthUser::getType,type);
        return oauthUserService.getOne(queryWrapper, false);
    }

    /**
     * 绑定
     * @param type
     * @param thirdAuthUser
     * @param client
     * @return
     */
    private AuthenticationToken bind(int type,ThirdAuthUser thirdAuthUser,Client client) {
        User user = new User();
        user.setNickname(thirdAuthUser.getNickname());
        user.setGender(UserConstant.GENDER_MALE);
        user.setBirthday(LocalDate.now());
        user.setEmail("".equals(thirdAuthUser.getEmail()) ? null : thirdAuthUser.getEmail());
        user.setAvatar(thirdAuthUser.getAvatar());
        user.setStatus(UserConstant.STATUS_NORMAL);
        user.setAdmin(UserConstant.ORDINARY);
        user.setCreateTime(LocalDateTime.now());
        userService.save(user);
        // 关联表
        OauthUser newOauthUser = new OauthUser();
        newOauthUser.setUuid(thirdAuthUser.getUuid());
        newOauthUser.setUserId(user.getId());
        newOauthUser.setType(type);
        newOauthUser.setCreateTime(LocalDateTime.now());
        oauthUserService.save(newOauthUser);
        PrimaryKeyAuthenticationToken authenticationToken = new PrimaryKeyAuthenticationToken(user.getId());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return tokenStore.storeToken(authenticate,client);
    }

}
