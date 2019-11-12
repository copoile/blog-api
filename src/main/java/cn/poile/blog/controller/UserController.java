package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.validator.IsPhone;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @PostMapping("/password/update")
    public ApiResponse updPassword(@NotBlank(message = "旧密码不能为空") @RequestParam(value = "oldPassword") String oldPassword,
                                      @NotBlank(message = "新密码不能为空") @Length(min = 6,message = "密码至少6位数") @RequestParam(value = "newPassword") String newPassword) {
        return createResponse();
    }

    @PostMapping("/password/reset")
    public ApiResponse rstPassword(@NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile, @NotBlank(message = "验证码不能为空") @RequestParam("code") String code,
                                     @NotBlank(message = "密码不能为空") @RequestParam("password") String password) {
        return createResponse();
    }

    @PostMapping("/avatar/update")
    public ApiResponse updAvatar(@RequestPart(value = "file") MultipartFile file) {
        return createResponse();
    }

    @PostMapping("/mobile/validate")
    public ApiResponse validateMobile(@NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile,
                                      @NotBlank(message = "验证码不能为空") @RequestParam("code") String code) {
        return createResponse();
    }

    @PostMapping("/mobile/rebind")
    public ApiResponse rebindMobile(@NotNull(message = "手机号不能为空") @IsPhone @RequestParam(value = "mobile") String mobile,
                                    @NotBlank(message = "验证码不能为空") @RequestParam(value = "code") String code) {
        return createResponse();
    }

    @PostMapping("/email/validate")
    @ApiOperation(value = "发送验证链接到邮箱")
    public ApiResponse validateEmail(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") @RequestParam("email")String email) {
        userService.validateEmail(email);
        return createResponse();
    }

    @PostMapping("/email/bind")
    public ApiResponse bindEmail(@NotBlank(message = "code不能为空") @RequestParam("code") String code) {
        userService.bindEmail(code);
        return createResponse();
    }


}
