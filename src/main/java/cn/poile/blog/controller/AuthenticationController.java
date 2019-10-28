package cn.poile.blog.controller;

import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.Token;
import cn.poile.blog.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yaohw
 * @create: 2019-10-25 10:55
 **/
@RestController
@Log4j2
public class AuthenticationController extends BaseController{


    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<Token> login(@RequestParam String username, @RequestParam String password) {
        Token tokenVo = authenticationService.usernameOrMobilePasswordAuthenticate(username, password);
        return createResponse(tokenVo);
    }
}
