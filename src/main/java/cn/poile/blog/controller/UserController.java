package cn.poile.blog.controller;


import cn.poile.blog.common.response.ApiResponse;
import cn.poile.blog.common.validator.annotation.IsImage;
import cn.poile.blog.common.validator.annotation.IsPhone;
import cn.poile.blog.controller.model.dto.AccessTokenDTO;
import cn.poile.blog.controller.model.request.UpdateUserRequest;
import cn.poile.blog.controller.model.request.UserRegisterRequest;
import cn.poile.blog.entity.User;
import cn.poile.blog.service.IUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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


    @GetMapping("/info")
    @ApiOperation(value = "获取用户信息", notes = "需要传accessToken")
    public ApiResponse<Object> info(Authentication authentication) {
        return createResponse(authentication.getPrincipal());
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "不需要传accessToken")
    public ApiResponse register(@Validated @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return createResponse();
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新用户基本信息", notes = "需要传accessToken，请求的json中id字段必传，更新不为null的项")
    public ApiResponse update(@RequestBody UpdateUserRequest request) {
        userService.update(request);
        return createResponse();
    }

    @PostMapping("/password/update")
    @ApiOperation(value = "修改密码", notes = "需要传accessToken,密码至少6位数")
    public ApiResponse updPassword(@ApiParam("原密码") @NotBlank(message = "旧密码不能为空") @RequestParam(value = "oldPassword") String oldPassword,
                                   @ApiParam("新密码") @NotBlank(message = "新密码不能为空") @Length(min = 6, message = "密码至少6位数") @RequestParam(value = "newPassword") String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return createResponse();
    }

    @PostMapping("/password/reset")
    @ApiOperation(value = "重置密码", notes = "不需要传accessToken,需要验证手机号")
    public ApiResponse resetPassword(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile,
                                     @ApiParam("验证码") @NotBlank(message = "验证码不能为空") @RequestParam("code") String code,
                                     @ApiParam("密码") @NotBlank(message = "密码不能为空") @Length(min = 6, message = "密码至少6位数") @RequestParam("password") String password) {
        userService.resetPassword(mobile, code, password);
        return createResponse();
    }

    @PostMapping("/avatar/update")
    @ApiOperation(value = "更新用户头像", notes = "文件只限bmp,gif,jpeg,jpeg,png,webp格式")
    public ApiResponse updAvatar(@ApiParam("头像图片文件") @IsImage @RequestPart(value = "file") MultipartFile file) {
        userService.updateAvatar(file);
        return createResponse();
    }

    @PostMapping("/mobile/validate")
    @ApiOperation(value = "更换手机号步骤一，验证原手机号", notes = "需要传accessToken")
    public ApiResponse validateMobile(@ApiParam("手机号") @NotNull(message = "手机号不能为空") @IsPhone @RequestParam("mobile") long mobile,
                                      @NotBlank(message = "验证码不能为空") @RequestParam("code") String code) {
        userService.validateMobile(mobile, code);
        return createResponse();
    }

    @PostMapping("/mobile/rebind")
    @ApiOperation(value = "更换手机号步骤二，绑定新手机号", notes = "需要传accessToken")
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

    @PostMapping("/email/bind")
    @ApiOperation(value = "code绑定邮箱", notes = "不需要accessToken，code有效2小时")
    public ApiResponse bindEmail(@ApiParam("邮箱链接中的code") @NotBlank(message = "code不能为空") @RequestParam("code") String code) {
        userService.bindEmail(code);
        return createResponse();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取用户信息，用于后台管理", notes = "需要accessToken，需要管理员权限")
    public ApiResponse<IPage<User>> page(@ApiParam("页码") @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                         @ApiParam("每页数量") @RequestParam(value = "size", required = false, defaultValue = "5") long size,
                                         @ApiParam("用户名") @RequestParam(value = "username", required = false) String username,
                                         @ApiParam("昵称") @RequestParam(value = "nickname", required = false) String nickname) {
        return createResponse(userService.page(current, size, username, nickname));
    }

    @PostMapping("/status/update")
    @ApiOperation(value = "修改用户状态,用于禁用、锁定用户等操作", notes = "需要accessToken，需要管理员权限")
    public ApiResponse status(@ApiParam("用户id") @RequestParam("userId") Integer userId,
                              @ApiParam("状态,0:正常，1:锁定，2:禁用，3:过期") @RequestParam("status") Integer status) {
        userService.status(userId, status);
        return createResponse();
    }

}
