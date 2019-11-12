package cn.poile.blog.controller;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.validator.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.service.AuthenticationService;
import cn.poile.blog.vo.CustomUserDetails;
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
    public ApiResponse<AccessTokenDTO> accountLogin(@NotBlank(message = "账号不能为空")@RequestParam String username,@NotBlank(message = "密码不能为空") @RequestParam String password,
                                                    @RequestHeader(value = "Authorization",required = false) String authorization) {
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
    public ApiResponse<AccessTokenDTO> mobileLogin(@NotNull(message = "手机号不能为空") @IsPhone @RequestParam long mobile,@NotBlank(message = "验证码不能为空") @RequestParam String code,
                                                   @RequestHeader(value = "Authorization",required = false) String authorization) {
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
    public ApiResponse logout(@NotBlank @RequestHeader(value = "Authorization") String authorization) {
        if (authorization.startsWith(TOKEN_TYPE)) {
            String accessToken = authorization.substring(7);
            authenticationService.remove(accessToken);
        }
        return createResponse();
    }

    @PostMapping("/refresh_access_token")
    public ApiResponse<AccessTokenDTO> refreshAccessToken(@NotBlank @RequestParam String refreshToken) {
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

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test")
    public ApiResponse test(Principal principal) {
        return createResponse();
    }

    @PreAuthorize("hasAuthority('delete_img')")
    @GetMapping("/test2")
    public ApiResponse test2(Principal principal) {

        return createResponse();
    }
}
