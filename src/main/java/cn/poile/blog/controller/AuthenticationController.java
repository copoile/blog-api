package cn.poile.blog.controller;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.security.RedisTokenStore;
import cn.poile.blog.common.sms.SmsCodeService;
import cn.poile.blog.common.validator.annotation.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.entity.Client;
import cn.poile.blog.service.AuthenticationService;
import cn.poile.blog.service.IClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: yaohw
 * @create: 2019-10-25 10:55
 **/
@RestController
@Log4j2
@Api(tags = "认证服务", value = "/")
public class AuthenticationController extends BaseController {

    private static final String TOKEN_TYPE = "Bearer";

    private static final String AUTHORIZATION_TYPE = "Basic";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTokenStore tokenStore;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private IClientService clientService;


    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/account/login")
    @ApiOperation(value = "账号密码登录", notes = "账号可以是用户名或手机号")
    public ApiResponse<AccessTokenDTO> accountLogin(@ApiParam("用户名或手机号") @NotBlank(message = "账号不能为空") @RequestParam String username,
                                                    @ApiParam("密码") @NotBlank(message = "密码不能为空") @RequestParam String password,
                                                    @ApiParam("客户端认证请求头") @RequestHeader(value = "Authorization") String authorization) {
        Client client = getAndValidatedClient(authorization);
        AuthenticationToken authenticationToken = authenticationService.usernameOrMobilePasswordAuthenticate(username, password, client);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken, response);
        return createResponse(response);
    }

    @PostMapping("/mobile/login")
    @ApiOperation(value = "手机号验证码登录", notes = "验证码调用发送验证码接口获取")
    public ApiResponse<AccessTokenDTO> mobileLogin(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam long mobile,
                                                   @ApiParam("手机号验证码") @NotBlank(message = "验证码不能为空") @RequestParam String code,
                                                   @ApiParam("客户端认证请求头") @RequestHeader(value = "Authorization") String authorization) {
        Client client = getAndValidatedClient(authorization);
        AuthenticationToken authenticationToken = authenticationService.mobileCodeAuthenticate(mobile, code, client);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken, response);
        return createResponse(response);
    }

    @DeleteMapping("/logout")
    @ApiOperation(value = "用户登出")
    public ApiResponse logout(@RequestHeader(value = "Authorization") String authorization,
                              @ApiParam("accessToken") @RequestParam("accessToken") String accessToken) {
        Client client = getAndValidatedClient(authorization);
        authenticationService.remove(accessToken, client);
        return createResponse();
    }

    @PostMapping("/refresh_access_token")
    @ApiOperation(value = "刷新accessToken")
    public ApiResponse<AccessTokenDTO> refreshAccessToken(
            @ApiParam("客户端认证请求头") @RequestHeader(value = "Authorization") String authorization,
            @ApiParam("refreshToken") @NotBlank(message = "refreshToken不能为空") @RequestParam("refreshToken") String refreshToken) {
        Client client = getAndValidatedClient(authorization);
        AuthenticationToken authenticationToken = authenticationService.refreshAccessToken(refreshToken, client);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken, response);
        return createResponse(response);
    }

    /**
     * 获取并校验client
     *
     * @param authorization
     * @return
     */
    private Client getAndValidatedClient(String authorization) {
        String[] clientIdAndClientSecret = extractClientIdAndClientSecret(authorization);
        String clientId = clientIdAndClientSecret[0];
        String clientSecret = clientIdAndClientSecret[1];
        Client client = clientService.getClientByClientId(clientId);
        if (client == null || !passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效客户端");
        }
        return client;
    }

    /**
     * 提取客户端id和客户端密码
     *
     * @param authorization
     * @return
     */
    private String[] extractClientIdAndClientSecret(String authorization) {
        if (!authorization.startsWith(AUTHORIZATION_TYPE)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效客户端");
        }
        String base64Data = authorization.substring(6);
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            String data = new String(decoder.decodeBuffer(base64Data));
            String separator = ":";
            String[] split = data.split(separator);
            int length = split.length;
            int matched = 2;
            if (length != matched) {
                throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效客户端");
            }
            return split;
        } catch (Exception e) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效客户端");
        }
    }
}
