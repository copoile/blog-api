package cn.poile.blog.common.security;import cn.poile.blog.vo.CustomUserDetails;import io.swagger.annotations.ApiModel;import io.swagger.annotations.ApiModelProperty;import lombok.Data;import lombok.experimental.Accessors;/** * @author: yaohw * @create: 2019-10-28 18:00 **/@Data@Accessors(chain = true)@ApiModel(value="AuthenticationToken", description="AuthenticationToken")public class AuthenticationToken {    @ApiModelProperty("accessToken")    private String accessToken;    @ApiModelProperty("token类型:Bearer")    private String tokenType = "Bearer";    @ApiModelProperty("时效")    private long expire;    @ApiModelProperty("refreshToken")    private String refreshToken;    @ApiModelProperty(hidden = true,value = "用户信息")    private CustomUserDetails principal;}