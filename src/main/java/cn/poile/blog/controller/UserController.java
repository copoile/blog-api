package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.service.IUserService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author yaohw
 * @since 2019-10-23
 */
@RestController
@RequestMapping("/user")
@Log4j2
@Api(tags = "用户服务",value = "/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @GetMapping("/info")
    public ApiResponse<Object> info(Authentication authentication) {
        return createResponse(authentication.getPrincipal());
    }

    @PostMapping("/register")
    public ApiResponse register(@Validated @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return createResponse();
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody UpdateUserRequest request) {
        userService.update(request);
        return createResponse();
    }

}
