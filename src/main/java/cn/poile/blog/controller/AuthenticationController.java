package cn.poile.blog.controller;

import cn.poile.blog.common.oss.StorageProperties;
import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.security.AccessToken;
import cn.poile.blog.service.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author: yaohw
 * @create: 2019-10-25 10:55
 **/
@RestController
@Log4j2
public class AuthenticationController extends BaseController{

    @Autowired
    private StorageProperties storageProperties;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AccessToken> login(@RequestParam String username, @RequestParam String password) {
        AccessToken accessToken = authenticationService.usernameOrMobilePasswordAuthenticate(username, password);
        return createResponse(accessToken);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test")
    public ApiResponse test(Principal principal) {
        return createResponse();
    }

    @PreAuthorize("hasAuthority('delete_img')")
    @GetMapping("/test2")
    public ApiResponse test2(Principal principal) {
        log.info("type"+storageProperties.getType());
        log.info("AccessKey1"+storageProperties.getLettuce().getAccessKey());
        log.info("AccessKey2"+ storageProperties.getNos().getAccessKey());
        return createResponse();
    }
}
