package cn.poile.blog.controller;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.validator.annotation.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.service.AuthenticationService;
import cn.poile.blog.vo.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.Principal;

/**
 * @author: yaohw
 * @create: 2019-10-25 10:55
 **/
@RestController
@Log4j2
@Api(tags = "认证服务",value = "/")
public class AuthenticationController extends BaseController{

    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private RedisTokenStore tokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmsCodeService smsCodeService;


    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/account/login")
    @ApiOperation(value = "账号密码登录",notes = "账号可以是用户名或手机号")
    public ApiResponse<AccessTokenDTO> accountLogin(@ApiParam("用户名或手机号") @NotBlank(message = "账号不能为空")@RequestParam String username,
                                                    @ApiParam("密码") @NotBlank(message = "密码不能为空") @RequestParam String password,
                                                    @ApiParam("请求头，可空，用于重复登录处理") @RequestHeader(value = "Authorization",required = false) String authorization) {
        AccessTokenDTO accessTokenDTO = passwordRepeatLoginHandle(authorization,password);
        if (accessTokenDTO != null) {
            return createResponse(accessTokenDTO);
        }
        AuthenticationToken authenticationToken = authenticationService.usernameOrMobilePasswordAuthenticate(username, password);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken,response);
        return createResponse(response);
    }

    @PostMapping("/mobile/login")
    @ApiOperation(value = "手机号验证码登录",notes = "验证码调用发送验证码接口获取")
    public ApiResponse<AccessTokenDTO> mobileLogin(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam long mobile,
                                                   @ApiParam("手机号验证码") @NotBlank(message = "验证码不能为空") @RequestParam String code,
                                                   @ApiParam("请求头，可空，用于重复登录处理") @RequestHeader(value = "Authorization",required = false) String authorization) {
        AccessTokenDTO accessTokenDTO = mobileRepeatLoginHandle(authorization,mobile,code);
        if (accessTokenDTO != null) {
            return createResponse(accessTokenDTO);
        }
        AuthenticationToken authenticationToken = authenticationService.mobileCodeAuthenticate(mobile, code);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken,response);
        return createResponse(response);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "用户登出",notes = "需要accessToken")
    public ApiResponse logout(@NotBlank @RequestHeader(value = "Authorization") String authorization) {
        if (authorization.startsWith(TOKEN_TYPE)) {
            String accessToken = authorization.substring(7);
            authenticationService.remove(accessToken);
        }
        return createResponse();
    }

    @PostMapping("/refresh_access_token")
    @ApiOperation(value = "刷新accessToken")
    public ApiResponse<AccessTokenDTO> refreshAccessToken(@ApiParam("refreshToken") @NotBlank(message = "refreshToken不能为空") @RequestParam("refreshToken") String refreshToken) {
        AuthenticationToken authenticationToken = authenticationService.refreshAccessToken(refreshToken);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken,response);
        return createResponse(response);
    }


    /**
     * 密码模式重复登录
     * @param authorization
     * @return
     */
    private AccessTokenDTO passwordRepeatLoginHandle(final String authorization,String password) {
        if (authorization != null && authorization.startsWith(TOKEN_TYPE)) {
            String accessToken = authorization.substring(7);
            AuthenticationToken authenticationToken = tokenStore.readAccessToken(accessToken);
            if (authenticationToken != null) {
                CustomUserDetails principal = authenticationToken.getPrincipal();
                String principalPassword = principal.getPassword();
                boolean matches = passwordEncoder.matches(password, principalPassword);
                if (!matches) {
                    throw new ApiException(ErrorEnum.BAD_CREDENTIALS.getErrorCode(),ErrorEnum.BAD_CREDENTIALS.getErrorMsg());
                }
                AccessTokenDTO response = new AccessTokenDTO();
                BeanUtils.copyProperties(authenticationToken,response);
                return response;
            }
        }
        return null;
    }

    /**
     * 手机号验证码重复登录
     * @param authorization
     * @return
     */
    private AccessTokenDTO mobileRepeatLoginHandle(final String authorization,long mobile,String code) {
        if (authorization != null && authorization.startsWith(TOKEN_TYPE)) {
            String accessToken = authorization.substring(7);
            AuthenticationToken authenticationToken = tokenStore.readAccessToken(accessToken);
            if (authenticationToken != null) {
                if (!smsCodeService.checkSmsCode(mobile, code)) {
                    throw new ApiException(ErrorEnum.BAD_MOBILE_CODE.getErrorCode(),ErrorEnum.BAD_MOBILE_CODE.getErrorMsg());
                }
                AccessTokenDTO response = new AccessTokenDTO();
                BeanUtils.copyProperties(authenticationToken,response);
                smsCodeService.deleteSmsCode(mobile);
                return response;
            }
        }
        return null;
    }
}
