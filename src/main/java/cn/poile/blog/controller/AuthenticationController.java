package cn.poile.blog.controller;

import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AccessToken;
import cn.poile.blog.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
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

    @PostMapping("/login")
    public ApiResponse<AccessToken> login(@RequestParam String username, @RequestParam String password) {
        AccessToken accessToken = authenticationService.usernameOrMobilePasswordAuthenticate(username, password);
        return createResponse(accessToken);
    }

    @PostMapping("/logOut")
    public ApiResponse logout(@NotBlank @RequestHeader(value = "Authorization") String authorization) {
        if (authorization.startsWith(TOKEN_TYPE)) {
            String accessToken = authorization.substring(7);
            authenticationService.remove(accessToken);
        }
        return createResponse();
    }

    @PostMapping("/refresh_access_token")
    public ApiResponse<AccessToken> refreshAccessToken(@NotBlank @RequestParam String refreshToken) {
        return createResponse(authenticationService.refreshAccessToken(refreshToken));
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
