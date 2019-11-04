package cn.poile.blog.controller.model.request;

import cn.poile.blog.common.validator.IsPhone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
/**
 * @author: yaohw
 * @create: 2019-10-04 22:13
 */
@ApiModel(value = "用户注册请json",description = "用户注册")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("账号只能字母开头，允许5-16字节，允许字母数字下划线")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$", message = "账号只能字母开头，允许5-16字节，允许字母数字下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty("密码")
    private String password;

    @NotNull(message = "手机号不能为空")
    @IsPhone
    @ApiModelProperty("手机号")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String code;
}
