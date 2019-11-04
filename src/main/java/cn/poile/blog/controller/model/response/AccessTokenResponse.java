package cn.poile.blog.controller.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: yaohw
 * @create: 2019-11-04 17:03
 **/
@Data
@Accessors(chain = true)
@ApiModel(value="AccessTokenResponse", description="AccessTokenResponse")
public class AccessTokenResponse {
    @ApiModelProperty("accessToken")
    private String accessToken;

    @ApiModelProperty("token类型:Bearer")
    private String tokenType = "Bearer";

    @ApiModelProperty("时效")
    private long expire;

    @ApiModelProperty("refreshToken")
    private String refreshToken;
}
