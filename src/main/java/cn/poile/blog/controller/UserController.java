package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.validator.annotation.IsImage;
import cn.poile.blog.common.validator.annotation.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
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
@Api(tags = "用户服务", value = "/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;


    @ApiOperation(value = "获取用户信息", notes = "需要传accessToken")
    @GetMapping("/info")
    public ApiResponse<Object> info(Authentication authentication) {
        return createResponse(authentication.getPrincipal());
    }

    @ApiOperation(value = "用户注册", notes = "不需要传accessToken")
    @PostMapping("/register")
    public ApiResponse register(@Validated @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return createResponse();
    }

    @ApiOperation(value = "更新用户基本信息", notes = "需要传accessToken，请求的json中id字段必传，更新不为null的项")
    @PostMapping("/update")
    public ApiResponse update(@RequestBody UpdateUserRequest request) {
        userService.update(request);
        return createResponse();
    }

    @ApiOperation(value = "修改密码", notes = "需要传accessToken,密码至少6位数")
    @PostMapping("/password/update")
    public ApiResponse updPassword(@ApiParam("原密码") @NotBlank(message = "旧密码不能为空") @RequestParam(value = "oldPassword") String oldPassword,
                                   @ApiParam("新密码") @NotBlank(message = "新密码不能为空") @Length(min = 6, message = "密码至少6位数") @RequestParam(value = "newPassword") String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return createResponse();
    }

    @ApiOperation(value = "重置密码", notes = "不需要传accessToken,需要验证手机号")
    @PostMapping("/password/reset")
    public ApiResponse resetPassword(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile,
                                     @ApiParam("验证码") @NotBlank(message = "验证码不能为空") @RequestParam("code") String code,
                                     @ApiParam("密码") @NotBlank(message = "密码不能为空") @RequestParam("password") String password) {
        userService.resetPassword(mobile, code, password);
        return createResponse();
    }

    @ApiOperation(value = "更新用户头像", notes = "文件只限bmp,gif,jpeg,jpeg,png,webp格式")
    @PostMapping("/avatar/update")
    public ApiResponse updAvatar(@ApiParam("头像图片文件") @IsImage @RequestPart(value = "file") MultipartFile file) {
        userService.updateAvatar(file);
        return createResponse();
    }

    @ApiOperation(value = "更换手机号步骤一，验证原手机号", notes = "需要传accessToken")
    @PostMapping("/mobile/validate")
    public ApiResponse validateMobile(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile,
                                      @NotBlank(message = "验证码不能为空") @RequestParam("code") String code) {
        userService.validateMobile(mobile, code);
        return createResponse();
    }

    @ApiOperation(value = "更换手机号步骤二，绑定新手机号", notes = "需要传accessToken")
    @PostMapping("/mobile/rebind")
    public ApiResponse rebindMobile(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam(value = "mobile") long mobile,
                                    @ApiParam("验证码") @NotBlank(message = "验证码不能为空") @RequestParam(value = "code") String code) {
        userService.rebindMobile(mobile, code);
        return createResponse();
    }

    @PostMapping("/email/validate")
    @ApiOperation(value = "发送验证链接到邮箱", notes = "需要accessToken")
    public ApiResponse validateEmail(@ApiParam("邮箱") @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") @RequestParam("email") String email) {
        userService.validateEmail(email);
        return createResponse();
    }

    @ApiOperation(value = "code绑定邮箱", notes = "不需要accessToken，code有效2小时，绑定成功后返回accessToken相关信息")
    @PostMapping("/email/bind")
    public ApiResponse<AccessTokenDTO> bindEmail(@ApiParam("邮箱链接中的code") @NotBlank(message = "code不能为空") @RequestParam("code") String code) {
        return createResponse(userService.bindEmail(code));
    }


}
