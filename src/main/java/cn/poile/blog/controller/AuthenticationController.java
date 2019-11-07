package cn.poile.blog.controller;

import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AuthenticationToken;
import cn.poile.blog.common.validator.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private AuthenticationService authenticationService;

    @PostMapping("/account/login")
    public ApiResponse<AccessTokenDTO> accountLogin(@NotBlank(message = "账号不能为空")@RequestParam String username,@NotBlank(message = "密码不能为空") @RequestParam String password) {
        AuthenticationToken authenticationToken = authenticationService.usernameOrMobilePasswordAuthenticate(username, password);
        AccessTokenDTO response = new AccessTokenDTO();
        BeanUtils.copyProperties(authenticationToken,response);
        return createResponse(response);
    }

    @PostMapping("/mobile/login")
    public ApiResponse<AccessTokenDTO> mobileLogin(@NotNull(message = "手机号不能为空") @IsPhone @RequestParam long mobile,@NotBlank(message = "验证码不能为空") @RequestParam String code) {
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
